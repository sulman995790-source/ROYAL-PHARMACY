const {
  initializeTestEnvironment,
  assertFails,
  assertSucceeds,
} = require("@firebase/rules-unit-testing");
const { test, before, after, beforeEach } = require("node:test");
const fs = require("node:fs");

let testEnv;
const PROJECT_ID = process.env.GCP_PROJECT || "demo-no-project";
const ALICE_UID = "alice_pharmacist_01";
const BOB_UID = "bob_pharmacist_02";

const [emulatorHost, emulatorPortStr] = (
  process.env.FIRESTORE_EMULATOR_HOST || "127.0.0.1:8085"
).split(":");
const emulatorPort = parseInt(emulatorPortStr, 10);

before(async () => {
  const rules = fs.readFileSync("./firestore.rules", "utf8");
  testEnv = await initializeTestEnvironment({
    projectId: PROJECT_ID,
    firestore: {
      rules,
      host: emulatorHost,
      port: emulatorPort,
    },
  });
});

after(async () => {
  if (testEnv) {
    await testEnv.cleanup();
  }
});

beforeEach(async () => {
  if (testEnv) {
    await testEnv.clearFirestore();
  }
});

test("Unauthenticated user cannot read or write any collection", async () => {
  const unauthDb = testEnv.unauthenticatedContext().firestore();
  const past = new Date(Date.now() - 2000);

  await assertFails(unauthDb.collection(`users/${ALICE_UID}/inventory`).get());
  await assertFails(unauthDb.collection(`users/${ALICE_UID}/sales`).get());
  await assertFails(unauthDb.collection("pharmacy_inventory").get());
  await assertFails(
    unauthDb.doc(`users/${ALICE_UID}/inventory/medicine_1`).set({
      userId: ALICE_UID,
      name: "Dolo 650",
      stockPacks: 10,
      mrp: 34.0,
      createdAt: past,
      updatedAt: past,
    })
  );
});

test("Owner can create, partial-update, query, and delete inventory medicines matching Kotlin Sync", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  const created = new Date(Date.now() - 10000);

  await assertSucceeds(
    aliceDb.doc(`users/${ALICE_UID}/inventory/medicine_1`).set({
      userId: ALICE_UID,
      localId: 1,
      name: "Augmentin 625 Duo",
      manufacturer: "GSK",
      composition: "Amoxicillin 500mg + Clavulanic Acid 125mg",
      saltMolecule: "Amoxicillin + Clavulanic Acid",
      category: "Tablet",
      hsnCode: "3004",
      batchNumber: "AG625",
      expiryDate: "12/27",
      stockPacks: 25,
      mrp: 210.0,
      purchaseRate: 168.0,
      saleRate: 195.0,
      gstPercent: 12.0,
      rackLocation: "Rack A-1",
      scheduleDrug: "Schedule H",
      isEssential: true,
      isLifeSaving: false,
      minStockAlert: 5,
      _lastUpdatedBy: "ANDROID_APP",
      createdAt: created,
      updatedAt: created,
    })
  );

  // Partial update preserving createdAt and updating mutable stockPacks
  await assertSucceeds(
    aliceDb.doc(`users/${ALICE_UID}/inventory/medicine_1`).update({
      stockPacks: 22,
      rackLocation: "Rack B-2",
      updatedAt: new Date(Date.now() - 500),
    })
  );

  // Owner read & query succeeds
  await assertSucceeds(
    aliceDb.collection(`users/${ALICE_UID}/inventory`).get()
  );

  await assertSucceeds(
    aliceDb.doc(`users/${ALICE_UID}/inventory/medicine_1`).delete()
  );
});

test("Cross-user isolation: Bob cannot read or modify Alice's pharmacy records", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  const bobDb = testEnv.authenticatedContext(BOB_UID).firestore();
  const past = new Date(Date.now() - 5000);

  await assertSucceeds(
    aliceDb.doc(`users/${ALICE_UID}/sales/sale_101`).set({
      userId: ALICE_UID,
      localId: 101,
      invoiceNumber: "INV-2026-101",
      customerName: "Walk-in Patient",
      grandTotal: 450.0,
      paymentMode: "UPI",
      invoiceDate: "27-10-2026",
      itemCount: 2,
      _lastUpdatedBy: "ANDROID_APP",
      createdAt: past,
      updatedAt: past,
    })
  );

  await assertFails(bobDb.doc(`users/${ALICE_UID}/sales/sale_101`).get());
  await assertFails(bobDb.collection(`users/${ALICE_UID}/sales`).get());
  await assertFails(
    bobDb.doc(`users/${ALICE_UID}/sales/sale_101`).update({
      grandTotal: 9999.0,
      updatedAt: new Date(Date.now() - 500),
    })
  );
  await assertFails(bobDb.doc(`users/${ALICE_UID}/sales/sale_101`).delete());
});

test("Schema & immutable-field enforcement rejects forged timestamps, extra fields, and createdAt mutation", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  const past = new Date(Date.now() - 10000);
  const future = new Date(Date.now() + 60000);

  // Forged future timestamp rejected
  await assertFails(
    aliceDb.doc(`users/${ALICE_UID}/inventory/medicine_future`).set({
      userId: ALICE_UID,
      name: "Dolo 650",
      stockPacks: 5,
      mrp: 30.0,
      createdAt: future,
      updatedAt: past,
    })
  );

  // Unrecognized field rejected by hasOnly
  await assertFails(
    aliceDb.doc(`users/${ALICE_UID}/inventory/medicine_extra`).set({
      userId: ALICE_UID,
      name: "Dolo 650",
      stockPacks: 5,
      mrp: 30.0,
      adminOverride: true,
      createdAt: past,
      updatedAt: past,
    })
  );

  // Mismatched userId inside document body rejected
  await assertFails(
    aliceDb.doc(`users/${ALICE_UID}/inventory/medicine_spoof`).set({
      userId: BOB_UID,
      name: "Spoof Med",
      stockPacks: 1,
      mrp: 10.0,
      createdAt: past,
      updatedAt: past,
    })
  );

  // Valid create followed by attempt to mutate immutable createdAt rejected
  await assertSucceeds(
    aliceDb.doc(`users/${ALICE_UID}/config/business_settings`).set({
      userId: ALICE_UID,
      globalSafetyThreshold: 5,
      autoSyncEnabled: true,
      businessName: "ROYAL PHARMACY",
      _lastUpdatedBy: "ANDROID_APP",
      createdAt: past,
      updatedAt: past,
    })
  );

  await assertFails(
    aliceDb.doc(`users/${ALICE_UID}/config/business_settings`).update({
      createdAt: new Date(Date.now() - 100),
      updatedAt: new Date(Date.now() - 100),
    })
  );
});

test("Ledger records (customers, patients, doctors, suppliers, purchases, udhar) validate strictly", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  const past = new Date(Date.now() - 5000);

  await assertSucceeds(
    aliceDb.doc(`users/${ALICE_UID}/customers/customer_1`).set({
      userId: ALICE_UID,
      entityType: "CUSTOMER",
      localId: 1,
      action: "INSERT",
      payloadJson: JSON.stringify({ id: 1, name: "Ramesh Kumar" }),
      _lastUpdatedBy: "ANDROID_APP",
      createdAt: past,
      updatedAt: past,
    })
  );

  // Wrong entityType inside customers collection rejected
  await assertFails(
    aliceDb.doc(`users/${ALICE_UID}/customers/customer_bad`).set({
      userId: ALICE_UID,
      entityType: "DOCTOR",
      localId: 2,
      action: "INSERT",
      payloadJson: "{}",
      createdAt: past,
      updatedAt: past,
    })
  );
});
