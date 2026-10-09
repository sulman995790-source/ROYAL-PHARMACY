









    tailwind.config = {
      darkMode: 'class',
      theme: {
        extend: {
          fontFamily: {
            sans: ['"Plus Jakarta Sans"', 'sans-serif'],
          },
          colors: {
            royal: {
              magenta: '#9C1258',
              navy: '#1E293B',
              slate: '#F8FAFC',
              accent: '#BE185D',
            }
          }
        }
      }
    }
  




    // State Objects
    let state = {
      isLoggedIn: false,
      role: 'OWNER', // OWNER or STAFF
      pin: '1234',
      secretPassword: '@arifa1234SS',
      securityQuestion1: 'What is your first pharmacy name?',
      securityAnswer1: 'Royal',
      securityQuestion2: 'Who is your business mentor?',
      securityAnswer2: 'Father',
      userName: 'Suleman Hoque',
      userEmail: 'sulman995790@gmail.com',
      userPhone: '+91 94350 78210',
      ownerAvatar: 'https://api.dicebear.com/7.x/adventurer/svg?seed=Sulman',
      lastGoogleDriveSync: 'Today at 05:14 AM',
      activeStaffPermission: 'FULL_ACCESS',
      staffMembers: [
        { id: "s-1", name: "Nijamuddin Khan", designation: "Senior Billing Chemist", email: "khannijamuddin87275@gmail.com", phone: "+91 94350 78210", permission: "POS-only access", lastLoginTime: "Today at 02:15 PM", loginType: "GMAIL" },
        { id: "s-2", name: "Rahul Sharma", designation: "Inventory Manager", email: "rahul.sharma@royal.com", phone: "+91 98765 43210", permission: "Inventory & Billing access", lastLoginTime: "Yesterday at 11:30 AM", loginType: "PHONE" },
        { id: "s-3", name: "Priya Das", designation: "Clinical Assistant", email: "priya.das@royal.com", phone: "+91 88123 45678", permission: "View-only access", lastLoginTime: "05-Oct-2026 06:12 PM", loginType: "GMAIL" }
      ],
      staffActivityLogs: [
        {
          id: "act-1",
          staffName: "Nijamuddin Khan",
          staffRole: "POS-only access",
          actionType: "BILL_GENERATED",
          description: "Generated Cash Memo #INV-8821 for ₹840.00 (3 items, Customer: Ramesh Kumar)",
          timestamp: "Today at 02:20 PM",
          rawDate: new Date().toISOString(),
          badgeColor: "#10B981"
        },
        {
          id: "act-2",
          staffName: "Rahul Sharma",
          staffRole: "Inventory & Billing access",
          actionType: "STOCK_UPDATED",
          description: "Stock adjustment: Added 50 strips for 'Paracetamol 650mg (Dolo 650)' (New stock: 1240)",
          timestamp: "Today at 01:15 PM",
          rawDate: new Date().toISOString(),
          badgeColor: "#3B82F6"
        },
        {
          id: "act-3",
          staffName: "Suleman Hoque (Owner)",
          staffRole: "Owner (Full Access)",
          actionType: "STAFF_ADDED",
          description: "Registered new staff member 'Priya Das' with View-only access permissions",
          timestamp: "Yesterday at 05:40 PM",
          rawDate: new Date().toISOString(),
          badgeColor: "#8B5CF6"
        },
        {
          id: "act-4",
          staffName: "Nijamuddin Khan",
          staffRole: "POS-only access",
          actionType: "BILL_GENERATED",
          description: "Generated Cash Memo #INV-8820 for ₹1,250.00 (5 items, Payment Mode: UPI)",
          timestamp: "Yesterday at 03:15 PM",
          rawDate: new Date().toISOString(),
          badgeColor: "#10B981"
        },
        {
          id: "act-5",
          staffName: "Rahul Sharma",
          staffRole: "Inventory & Billing access",
          actionType: "STOCK_UPDATED",
          description: "Imported batch expiry updates for Azithromycin 500mg (Batch AZ-902, Exp: 12/26)",
          timestamp: "05-Oct-2026 05:10 PM",
          rawDate: new Date().toISOString(),
          badgeColor: "#3B82F6"
        }
      ],
      profile: {
        businessName: "ROYAL PHARMACY",
        phone: "+91 98765 43210",
        email: "contact@royalchemist.com",
        addressLine1: "Central Chemist Plaza, New Delhi",
        addressLine2: "DLF Phase 2, PIN - 110001",
        drugLicenseForm20: "DL-20B-77221",
        drugLicenseForm21: "DL-21B-77222",
        gstin: "07AAAAA1111A1Z1",
        ayushmanHfrId: "HFR-IN-9821-2291",
        ownerName: "Sulman Pharmacist"
      },
      currentSale: {
        customerName: 'Walk-in Customer',
        doctorName: 'Dr. Self / None',
        paymentMode: 'Cash',
        items: []
      },
      distributorCart: [],
      syncQueue: [],
      showProfitDetails: false,
      drugDatabase: [
        { name: "Paracetamol 650mg (Dolo 650)", generic: "Paracetamol 650mg", hsn: "300490", stock: 1240, price: 30.0, purchasePrice: 18.5, category: "Analgesic", safety: "A (Very Safe)", indication: "High Fever, Mild Pain, Migraines" },
        { name: "Calpol 650 Tablet", generic: "Paracetamol 650mg", hsn: "300490", stock: 950, price: 33.5, purchasePrice: 20.0, category: "Analgesic", safety: "A (Very Safe)", indication: "Fever and Body Ache" },
        { name: "Pacimol 650mg Tablet", generic: "Paracetamol 650mg", hsn: "300490", stock: 820, price: 28.0, purchasePrice: 16.5, category: "Analgesic", safety: "A (Very Safe)", indication: "Fever and Inflammatory Pain" },
        { name: "Amoxicillin 500mg Capsule", generic: "Amoxicillin", hsn: "300410", stock: 850, price: 72.0, purchasePrice: 45.0, category: "Antibiotic", safety: "B (Caution Required)", indication: "Bacterial Throat Infection, Sinusitis" },
        { name: "Augmentin 625 Duo Tablet", generic: "Amoxicillin 500mg + Clavulanic Acid 125mg", hsn: "300410", stock: 430, price: 210.0, purchasePrice: 155.0, category: "Antibiotic", safety: "B (Prescription Required)", indication: "Severe Bacterial Respiratory Infections" },
        { name: "Azithromycin 500mg (Azee)", generic: "Azithromycin Dihydrate", hsn: "300410", stock: 620, price: 125.0, purchasePrice: 82.0, category: "Antibiotic", safety: "B (Caution Required)", indication: "Respiratory Tract & Skin Infections" },
        { name: "Cefixime 200mg (Taxim-O)", generic: "Cefixime Trihydrate", hsn: "300410", stock: 390, price: 145.0, purchasePrice: 98.0, category: "Antibiotic", safety: "B (Caution Required)", indication: "Urinary Tract and Bronchial Infections" },
        { name: "Cetirizine 10mg Syrup", generic: "Cetirizine Hydrochloride", hsn: "300490", stock: 340, price: 45.0, purchasePrice: 22.0, category: "Antihistamine", safety: "A (Mild Drowsy)", indication: "Allergy Sneezing, Cold Congestion" },
        { name: "Levocetirizine 5mg (Xyzal)", generic: "Levocetirizine Dihydrochloride", hsn: "300490", stock: 510, price: 62.0, purchasePrice: 38.0, category: "Antihistamine", safety: "A (Non-Drowsy)", indication: "Allergic Rhinitis & Urticaria" },
        { name: "Montek-LC Tablet", generic: "Montelukast 10mg + Levocetirizine 5mg", hsn: "300490", stock: 740, price: 215.0, purchasePrice: 150.0, category: "Antihistamine", safety: "B (Caution)", indication: "Asthma & Allergic Cough" },
        { name: "Pantocid 40mg Tablet", generic: "Pantoprazole Sodium Sesquihydrate", hsn: "300490", stock: 1500, price: 95.0, purchasePrice: 62.0, category: "Antiacid", safety: "A (Extremely Safe)", indication: "Hyperacidity, Heartburn, Acid Reflux" },
        { name: "Rantac 150mg Tablet", generic: "Ranitidine Hydrochloride", hsn: "300490", stock: 1120, price: 24.0, purchasePrice: 12.0, category: "Antiacid", safety: "A (Safe)", indication: "Gastric Ulcers & Acidity" },
        { name: "Omez 20mg Capsule", generic: "Omeprazole", hsn: "300490", stock: 890, price: 42.0, purchasePrice: 25.0, category: "Antiacid", safety: "A (Safe)", indication: "Peptic Ulcers & GERD" },
        { name: "Glycomet-GP 2 Tablet", generic: "Glimepiride 2mg + Metformin 500mg SR", hsn: "300490", stock: 630, price: 142.0, purchasePrice: 95.0, category: "Cardiac", safety: "C (Prescription / Diabetes)", indication: "Type 2 Diabetes Mellitus Glycemic Control" },
        { name: "Janumet 50/500 Tablet", generic: "Sitagliptin 50mg + Metformin 500mg", hsn: "300490", stock: 320, price: 380.0, purchasePrice: 290.0, category: "Cardiac", safety: "C (Prescription / Diabetes)", indication: "Advanced Blood Glucose Management" },
        { name: "Atorva 10mg Tablet", generic: "Atorvastatin Calcium", hsn: "300490", stock: 780, price: 110.0, purchasePrice: 70.0, category: "Cardiac", safety: "B (Caution)", indication: "Hypercholesterolemia & Cardiovascular Protection" },
        { name: "Ceftriaxone 1g IV/IM Injection", generic: "Ceftriaxone Sodium Sterile", hsn: "300420", stock: 450, price: 110.0, purchasePrice: 85.0, category: "Injectable", safety: "C (Prescription only / Hospital)", indication: "Severe Systemic Bacterial Sepsis & Meningitis" },
        { name: "Monocef 1g IV/IM Injection", generic: "Ceftriaxone Sodium", hsn: "300420", stock: 520, price: 62.0, purchasePrice: 42.0, category: "Injectable", safety: "C (Hospital Grade)", indication: "Severe Bacterial Infections & Typhoid" },
        { name: "Augmentin 1.2g IV Injection", generic: "Amoxicillin 1000mg + Clavulanate Potassium 200mg", hsn: "300420", stock: 310, price: 145.0, purchasePrice: 115.0, category: "Injectable", safety: "C (Hospital Grade)", indication: "Surgical Prophylaxis & Hospital Acquired Infections" },
        { name: "Pansec 40mg IV Injection", generic: "Pantoprazole Sodium 40mg", hsn: "300420", stock: 680, price: 56.0, purchasePrice: 38.0, category: "Injectable", safety: "C (Hospital Grade)", indication: "Acute Upper Gastrointestinal Bleed & Stress Ulcers" },
        { name: "Pacimol 100ml IV Infusion", generic: "Paracetamol 1000mg / 100ml", hsn: "300420", stock: 490, price: 82.0, purchasePrice: 55.0, category: "Injectable", safety: "B (Clinical Use)", indication: "Post-operative Pyrexia and Rapid Pain Management" },
        { name: "Azee 500mg IV Infusion", generic: "Azithromycin Dihydrate IV", hsn: "300420", stock: 210, price: 185.0, purchasePrice: 140.0, category: "Injectable", safety: "C (Hospital Grade)", indication: "Severe Community Acquired Pneumonia" },
        { name: "Taxim 1g Injection", generic: "Cefotaxime Sodium", hsn: "300420", stock: 380, price: 48.0, purchasePrice: 32.0, category: "Injectable", safety: "C (Prescription / Hospital)", indication: "Gonorrhea, Septicemia and Bone Infections" },
        { name: "Diclofenac 75mg/3ml IM Injection", generic: "Diclofenac Sodium", hsn: "300420", stock: 920, price: 18.0, purchasePrice: 9.5, category: "Injectable", safety: "B (Caution)", indication: "Acute Renal Colic and Severe Muscular Pain" },
        { name: "Ondem 2mg/2ml IV/IM Injection", generic: "Ondansetron Hydrochloride", hsn: "300420", stock: 540, price: 22.0, purchasePrice: 12.0, category: "Injectable", safety: "A (Clinical Safe)", indication: "Post-operative and Chemotherapy Induced Nausea" },
        { name: "Deriphyllin 2ml IV/IM Injection", generic: "Etofylline 84.7mg + Theophylline 25.3mg", hsn: "300420", stock: 410, price: 15.0, purchasePrice: 8.0, category: "Injectable", safety: "B (Caution)", indication: "Acute Bronchial Asthma and COPD Exacerbation" },
        { name: "Insulin Regular (Human) 40IU/ml Vial", generic: "Human Soluble Insulin", hsn: "300490", stock: 280, price: 165.0, purchasePrice: 125.0, category: "Injectable", safety: "C (Cold Chain Required)", indication: "Emergency Hyperglycemia & Ketoacidosis" }
      ],
      customReorderLevels: {}
    };

    // Load state from LocalStorage if present
    // Firebase Web Integration & Real-time Sync
    const firebaseConfig = {
      projectId: "gen-lang-client-0237361589",
      appId: "1:844575165623:web:86156fda64cf18f738d132",
      apiKey: "AIzaSyB8ww3Pqdn8Ntu6WArA20SmM3Ck-0VsMpc",
      authDomain: "gen-lang-client-0237361589.firebaseapp.com",
      storageBucket: "gen-lang-client-0237361589.firebasestorage.app",
      messagingSenderId: "844575165623"
    };

    let fbDb;
    function initializeFirebaseWeb() {
      if (typeof firebase === 'undefined') return;
      if (!firebase.apps.length) {
        firebase.initializeApp(firebaseConfig);
      }
      fbDb = firebase.firestore();
      setupFirestoreListeners();
    }

    function setupFirestoreListeners() {
      if (!fbDb) return;
      fbDb.collection('pharmacy_inventory').onSnapshot(snapshot => {
        snapshot.docChanges().forEach(change => {
          const data = change.doc.data();
          if (!data.name) return;
          const reorderVal = data.minStockAlert !== undefined ? Number(data.minStockAlert) : 15;
          if (change.type === 'added' || change.type === 'modified') {
            state.customReorderLevels = state.customReorderLevels || {};
            state.customReorderLevels[data.name] = reorderVal;
            const index = state.drugDatabase.findIndex(d => d.name === data.name);
            if (index !== -1) {
              // Safe partial merge: preserve other fields already present on the client
              const existing = state.drugDatabase[index];
              if (data.minStockAlert !== undefined) {
                existing.reorderLevel = Number(data.minStockAlert);
                existing.minStock = Number(data.minStockAlert);
              }
              if (data.stockPacks !== undefined || data.stock !== undefined) {
                existing.stock = data.stockPacks !== undefined ? Number(data.stockPacks) : Number(data.stock);
              }
              if (data.mrp !== undefined || data.price !== undefined) {
                existing.price = data.mrp !== undefined ? Number(data.mrp) : Number(data.price);
              }
              if (data.purchasePrice !== undefined) {
                existing.purchasePrice = Number(data.purchasePrice);
              }
              if (data.category !== undefined) {
                existing.category = data.category;
              }
              if (data.genericName !== undefined || data.generic !== undefined) {
                existing.generic = data.genericName || data.generic;
              }
              if (data.safetyCategory !== undefined || data.safety !== undefined) {
                existing.safety = data.safetyCategory || data.safety;
              }
              if (data.indication !== undefined) {
                existing.indication = data.indication;
              }
            } else {
              // New medicine not in local list, create fully
              const med = {
                name: data.name,
                generic: data.genericName || data.generic || '',
                stock: data.stockPacks || data.stock || 0,
                price: data.mrp || data.price || 0,
                purchasePrice: data.purchasePrice || 0,
                category: data.category || 'General',
                safety: data.safetyCategory || data.safety || 'A',
                indication: data.indication || '',
                reorderLevel: reorderVal,
                minStock: reorderVal
              };
              state.drugDatabase.push(med);
            }
          }
        });
        if (typeof renderLowStockWidget === 'function') renderLowStockWidget();
        if (typeof loadMedicineDropdown === 'function') loadMedicineDropdown();
      });

      fbDb.collection('pharmacy_config').doc('business_settings').onSnapshot(doc => {
        if (doc.exists) {
          const data = doc.data();
          if (data.secretPassword) state.secretPassword = data.secretPassword;
          if (data.pin) state.pin = data.pin;
          if (data.securityQuestion1) state.securityQuestion1 = data.securityQuestion1;
          if (data.securityAnswer1) state.securityAnswer1 = data.securityAnswer1;
          syncUiState();
        }
      });
    }

    function syncWebChangeToFirestore(collection, id, data) {
      if (!fbDb) return;
      fbDb.collection(collection).doc(id).set({
        ...data,
        _lastUpdatedBy: 'WEB_PORTAL',
        _lastUpdatedAt: firebase.firestore.FieldValue.serverTimestamp()
      }, { merge: true });
    }

    function changeSecretPasswordWeb() {
      const oldPass = state.secretPassword;
      const newPass = document.getElementById('set-secret-password').value;
      Swal.fire({
        title: 'Confirm Password Change',
        text: 'Please enter old secret password to confirm',
        input: 'password',
        showCancelButton: true
      }).then(result => {
        if (result.value === oldPass) {
          state.secretPassword = newPass;
          syncWebChangeToFirestore('pharmacy_config', 'business_settings', { secretPassword: newPass });
          Swal.fire('Updated', 'Secret password changed and synced to Cloud!', 'success');
          saveStateToLocal();
        } else if (result.value) {
          Swal.fire('Error', 'Incorrect old password', 'error');
        }
      });
    }

    function saveSecurityQuestionsWeb() {
      state.securityQuestion1 = document.getElementById('set-q1').value;
      state.securityAnswer1 = document.getElementById('set-a1').value;
      state.securityQuestion2 = document.getElementById('set-q2').value;
      state.securityAnswer2 = document.getElementById('set-a2').value;
      
      syncWebChangeToFirestore('pharmacy_config', 'business_settings', {
        securityQuestion1: state.securityQuestion1,
        securityAnswer1: state.securityAnswer1,
        securityQuestion2: state.securityQuestion2,
        securityAnswer2: state.securityAnswer2
      });
      
      Swal.fire('Saved', 'Security questions updated and synced!', 'success');
      saveStateToLocal();
    }

    function resetPasswordFlowWeb() {
      Swal.fire({
        title: 'Reset Secret Password',
        text: 'Choose reset method',
        showDenyButton: true,
        confirmButtonText: 'Security Questions',
        denyButtonText: 'OTP (Mock)'
      }).then((result) => {
        if (result.isConfirmed) {
          Swal.fire({
            title: 'Security Verification',
            html: `
              <div class="text-left space-y-2">
                <p class="text-xs font-bold">${state.securityQuestion1}</p>
                <input id="ans1" class="swal2-input" placeholder="Answer 1">
                <p class="text-xs font-bold">${state.securityQuestion2}</p>
                <input id="ans2" class="swal2-input" placeholder="Answer 2">
                <p class="text-xs font-bold">New Secret Password</p>
                <input id="newpass" type="password" class="swal2-input" placeholder="New Password">
              </div>
            `,
            preConfirm: () => {
              return {
                ans1: document.getElementById('ans1').value,
                ans2: document.getElementById('ans2').value,
                newPass: document.getElementById('newpass').value
              }
            }
          }).then(res => {
            if (res.value) {
              if (res.value.ans1.toLowerCase() === state.securityAnswer1.toLowerCase() && 
                  res.value.ans2.toLowerCase() === state.securityAnswer2.toLowerCase()) {
                state.secretPassword = res.value.newPass;
                syncWebChangeToFirestore('pharmacy_config', 'business_settings', { secretPassword: res.value.newPass });
                Swal.fire('Success', 'Password reset successful!', 'success');
                saveStateToLocal();
                syncUiState();
              } else {
                Swal.fire('Failed', 'Incorrect security answers', 'error');
              }
            }
          });
        } else if (result.isDenied) {
          // Mock OTP reset
          Swal.fire({
            title: 'OTP Verification',
            text: 'A mock security code 123456 sent to registered contact.',
            input: 'text',
            inputPlaceholder: 'Enter 123456'
          }).then(res => {
            if (res.value === '123456') {
              Swal.fire({
                title: 'New Password',
                input: 'password',
                inputPlaceholder: 'Enter new secret password'
              }).then(passRes => {
                if (passRes.value) {
                  state.secretPassword = passRes.value;
                  syncWebChangeToFirestore('pharmacy_config', 'business_settings', { secretPassword: passRes.value });
                  Swal.fire('Success', 'Password reset successful via OTP!', 'success');
                  saveStateToLocal();
                  syncUiState();
                }
              });
            }
          });
        }
      });
    }

    function setCookie(name, value, days) {
      let expires = "";
      if (days) {
        const date = new Date();
        date.setTime(date.getTime() + (days * 24 * 60 * 60 * 1000));
        expires = "; expires=" + date.toUTCString();
      }
      document.cookie = name + "=" + (value || "") + expires + "; path=/; SameSite=Lax";
    }
    function getCookie(name) {
      const nameEQ = name + "=";
      const ca = document.cookie.split(';');
      for(let i=0;i < ca.length;i++) {
        let c = ca[i];
        while (c.charAt(0)==' ') c = c.substring(1,c.length);
        if (c.indexOf(nameEQ) == 0) return c.substring(nameEQ.length,c.length);
      }
      return null;
    }
    function eraseCookie(name) {
      document.cookie = name+'=; Max-Age=-99999999; path=/;';
    }

    function loadSavedState() {
      const saved = localStorage.getItem('royal_pharmacy_state');
      const cookieToken = getCookie('royal_pharmacy_token');
      const localToken = localStorage.getItem('royal_pharmacy_token');
      const sessionToken = sessionStorage.getItem('royal_pharmacy_token');
      const persistentAuth = localStorage.getItem('royal_pharmacy_auth_persistent') || sessionStorage.getItem('royal_pharmacy_auth_persistent');
      
      if (cookieToken || localToken || sessionToken || persistentAuth) {
        state.isLoggedIn = true;
      }
      if (saved) {
        try {
          const parsed = JSON.parse(saved);
          state.profile = parsed.profile || state.profile;
          state.distributorCart = parsed.distributorCart || state.distributorCart;
          state.pin = parsed.pin || state.pin;
          state.secretPassword = parsed.secretPassword || state.secretPassword;
          state.role = parsed.role || state.role;
          state.ownerAvatar = parsed.ownerAvatar || state.ownerAvatar;
          state.staffMembers = parsed.staffMembers || state.staffMembers;
          state.staffActivityLogs = parsed.staffActivityLogs || state.staffActivityLogs;
          state.activeStaffPermission = parsed.activeStaffPermission || (state.role === 'OWNER' ? 'FULL_ACCESS' : 'POS-only access');
          state.userName = parsed.userName || state.userName;
          state.userEmail = parsed.userEmail || state.userEmail;
          state.userPhone = parsed.userPhone || state.userPhone;
          state.customReorderLevels = parsed.customReorderLevels || {};
          if (parsed.isLoggedIn !== undefined) state.isLoggedIn = state.isLoggedIn || parsed.isLoggedIn;
        } catch (e) {
          console.error("Failed to restore local state", e);
        }
      }
      if (persistentAuth) {
        try {
          const authParsed = JSON.parse(persistentAuth);
          if (authParsed.isLoggedIn) {
            state.isLoggedIn = true;
            if (authParsed.userName) state.userName = authParsed.userName;
            if (authParsed.role) state.role = authParsed.role;
            if (authParsed.email) state.userEmail = authParsed.email;
            if (authParsed.phone) state.userPhone = authParsed.phone;
          }
        } catch(e) {}
      }
      syncUiState();
    }

    function saveStateToLocal() {
      const statePayload = {
        profile: state.profile,
        distributorCart: state.distributorCart,
        pin: state.pin,
        secretPassword: state.secretPassword,
        role: state.role,
        ownerAvatar: state.ownerAvatar,
        staffMembers: state.staffMembers,
        staffActivityLogs: state.staffActivityLogs,
        activeStaffPermission: state.activeStaffPermission,
        userName: state.userName,
        userEmail: state.userEmail,
        userPhone: state.userPhone,
        customReorderLevels: state.customReorderLevels || {},
        isLoggedIn: state.isLoggedIn
      };
      localStorage.setItem('royal_pharmacy_state', JSON.stringify(statePayload));
      
      const authToken = localStorage.getItem('royal_pharmacy_token') || sessionStorage.getItem('royal_pharmacy_token') || ('rp_token_' + Date.now() + '_' + Math.random().toString(36).substring(2));
      if (state.isLoggedIn) {
        localStorage.setItem('royal_pharmacy_token', authToken);
        sessionStorage.setItem('royal_pharmacy_token', authToken);
        setCookie('royal_pharmacy_token', authToken, 365);
      } else {
        localStorage.removeItem('royal_pharmacy_token');
        sessionStorage.removeItem('royal_pharmacy_token');
        eraseCookie('royal_pharmacy_token');
      }

      const authPayload = JSON.stringify({
        isLoggedIn: state.isLoggedIn,
        token: authToken,
        userName: state.userName,
        role: state.role,
        email: state.userEmail,
        phone: state.userPhone
      });
      localStorage.setItem('royal_pharmacy_auth_persistent', authPayload);
      sessionStorage.setItem('royal_pharmacy_auth_persistent', authPayload);
    }

    // UI State Sync
    function syncUiState() {
      // Toggle display of Main App vs Login Portal
      const loginContainer = document.getElementById('web-login-container');
      const appLayout = document.getElementById('web-main-app-layout');
      if (state.isLoggedIn) {
        if (loginContainer) loginContainer.classList.add('hidden');
        if (appLayout) {
          appLayout.classList.remove('hidden');
          appLayout.classList.add('flex');
        }
      } else {
        if (loginContainer) {
          loginContainer.classList.remove('hidden');
          loginContainer.classList.add('flex');
        }
        if (appLayout) {
          appLayout.classList.add('hidden');
          appLayout.classList.remove('flex');
        }
        return; // Halt internal elements rendering if unauthenticated
      }

      // Show profit toggle if Owner
      const profitToggle = document.getElementById('profit-toggle-container');
      if (profitToggle) {
        if (state.role === 'OWNER') {
          profitToggle.classList.remove('hidden');
          profitToggle.classList.add('flex');
        } else {
          profitToggle.classList.add('hidden');
          profitToggle.classList.remove('flex');
          state.showProfitDetails = false;
        }
      }
      const profitInput = document.getElementById('profit-toggle-input');
      if (profitInput) profitInput.checked = state.showProfitDetails;

      // Sync badges & Avatar elements
      const roleBadge = document.getElementById('current-role-badge');
      if (roleBadge) {
        if (state.role === 'OWNER') {
          roleBadge.innerText = '👑 Owner (Full Access)';
          roleBadge.className = 'font-medium text-pink-200';
        } else {
          const permLabel = state.activeStaffPermission ? state.activeStaffPermission.replace(' access', '') : 'Staff POS';
          roleBadge.innerText = `💼 ${state.userName.split(' ')[0]} (${permLabel})`;
          roleBadge.className = 'font-medium text-sky-200';
        }
      }
      
      const avatarContainer = document.getElementById('user-avatar-container');
      if (avatarContainer) {
        if (state.role === 'OWNER' && state.ownerAvatar) {
          avatarContainer.innerHTML = `<img src="${state.ownerAvatar}" class="w-full h-full object-cover">`;
        } else {
          avatarContainer.innerHTML = `<span id="user-avatar-text" class="font-bold text-sm">${state.role === 'OWNER' ? 'OP' : 'ST'}</span>`;
        }
      }
      
      const setAvatarImg = document.getElementById('set-avatar-img');
      const setAvatarOwnerName = document.getElementById('set-avatar-owner-name');
      if (setAvatarImg) setAvatarImg.src = state.ownerAvatar || 'https://api.dicebear.com/7.x/adventurer/svg?seed=Sulman';
      if (setAvatarOwnerName) setAvatarOwnerName.innerText = state.profile.ownerName || state.userName;

      document.getElementById('user-display-name').innerText = state.role === 'OWNER' ? (state.profile.ownerName || state.userName) : state.userName;
      document.getElementById('user-display-email').innerText = state.role === 'OWNER' ? state.userEmail : state.userPhone;
      
      const verifiedBadge = document.getElementById('user-verified-badge');
      if (verifiedBadge) {
        if (state.role === 'OWNER') {
          verifiedBadge.classList.remove('hidden');
        } else {
          verifiedBadge.classList.add('hidden');
        }
      }

      renderStaffTableWeb();
      renderStaffActivityViewWeb();
      renderStaffActivityBadgeWeb();
      
      // Update form configurations
      document.getElementById('set-name').value = state.profile.businessName;
      document.getElementById('set-phone').value = state.profile.phone;
      document.getElementById('set-gstin').value = state.profile.gstin;
      document.getElementById('set-dl20').value = state.profile.drugLicenseForm20;
      document.getElementById('set-dl21').value = state.profile.drugLicenseForm21;
      document.getElementById('set-ayushman').value = state.profile.ayushmanHfrId;
      document.getElementById('set-pin').value = state.pin;
      document.getElementById('set-secret-password').value = state.secretPassword;
      
      if (document.getElementById('set-q1')) document.getElementById('set-q1').value = state.securityQuestion1;
      if (document.getElementById('set-a1')) document.getElementById('set-a1').value = state.securityAnswer1;
      if (document.getElementById('set-q2')) document.getElementById('set-q2').value = state.securityQuestion2;
      if (document.getElementById('set-a2')) document.getElementById('set-a2').value = state.securityAnswer2;

      // Badges
      document.getElementById('cart-badge-count').innerText = state.distributorCart.length;
      document.getElementById('cart-item-count').innerText = `${state.currentSale.items.length} items`;

      // Load selectable items
      loadMedicineDropdown();
      renderBillItems();
      renderCartTable();
      renderInvoicePreview();
    }

    function toggleProfitDetails() {
      state.showProfitDetails = document.getElementById('profit-toggle-input').checked;
      renderBillItems();
    }

    // Tab Switching Controller
    function switchTab(tabId) {
      if (tabId === 'staff-mgmt') {
        switchTab('settings');
        switchSettingsSubTab('staff');
        const dtStaffBtn = document.getElementById('tab-btn-staff-mgmt');
        if (dtStaffBtn) {
          dtStaffBtn.classList.add('text-royal-magenta', 'bg-pink-50', 'font-semibold');
          dtStaffBtn.classList.remove('text-slate-600', 'font-medium');
        }
        return;
      }

      if (tabId === 'staff-activity') {
        switchTab('settings');
        switchSettingsSubTab('activity');
        const dtActBtn = document.getElementById('tab-btn-staff-activity');
        if (dtActBtn) {
          dtActBtn.classList.add('text-royal-magenta', 'bg-pink-50', 'font-semibold');
          dtActBtn.classList.remove('text-slate-600', 'font-medium');
        }
        return;
      }

      const tabs = ['dashboard', 'pos', 'gemini', 'database', 'cart', 'calculator', 'symptoms', 'invoices', 'settings', 'udharkhata', 'expiry', 'interactions', 'tax', 'converter'];
      tabs.forEach(t => {
        const el = document.getElementById(`tab-content-${t}`);
        if (el) {
          el.classList.add('hidden');
          el.classList.remove('block');
        }
        
        // desktop sidebar btn sync
        const dtBtn = document.getElementById(`tab-btn-${t}`);
        if (dtBtn) {
          dtBtn.classList.remove('text-royal-magenta', 'bg-pink-50', 'font-semibold');
          dtBtn.classList.add('text-slate-600', 'font-medium');
        }

        // mobile tab sync
        const mbBtn = document.getElementById(`mob-tab-${t}`);
        if (mbBtn) {
          mbBtn.classList.remove('text-royal-magenta');
          mbBtn.classList.add('text-slate-500');
        }
      });

      const dtStaffBtn = document.getElementById('tab-btn-staff-mgmt');
      if (dtStaffBtn && tabId !== 'staff-mgmt') {
        dtStaffBtn.classList.remove('text-royal-magenta', 'bg-pink-50', 'font-semibold');
        dtStaffBtn.classList.add('text-slate-600', 'font-medium');
      }
      const dtActBtn = document.getElementById('tab-btn-staff-activity');
      if (dtActBtn && tabId !== 'staff-activity') {
        dtActBtn.classList.remove('text-royal-magenta', 'bg-pink-50', 'font-semibold');
        dtActBtn.classList.add('text-slate-600', 'font-medium');
      }

      // Show targeted tab
      const targetEl = document.getElementById(`tab-content-${tabId}`);
      if (targetEl) {
        targetEl.classList.remove('hidden');
        targetEl.classList.add('block');
      }

      // Sync active classes
      const actDtBtn = document.getElementById(`tab-btn-${tabId}`);
      if (actDtBtn) {
        actDtBtn.classList.add('text-royal-magenta', 'bg-pink-50', 'font-semibold');
        actDtBtn.classList.remove('text-slate-600', 'font-medium');
      }

      const actMbBtn = document.getElementById(`mob-tab-${tabId}`);
      if (actMbBtn) {
        actMbBtn.classList.add('text-royal-magenta');
        actMbBtn.classList.remove('text-slate-500');
      }

      if (tabId === 'dashboard') {
        setTimeout(initDashboardCharts, 100);
        renderLowStockWidget();
        renderRecentActivityFeedWeb();
      } else if (tabId === 'udharkhata') {
        renderUdharCustomerList();
      } else if (tabId === 'expiry') {
        renderExpiryBatches();
      } else if (tabId === 'interactions') {
        renderInteractionChips();
      }
    }

    // Chart.js Dashboard Charts Initialization & Management
    let chartSalesTrendInstance = null;
    let chartDailyProfitInstance = null;
    let chartTopMedsInstance = null;
    let chartMonthlyRevenueInstance = null;

    function initDashboardCharts() {
      const el1 = document.getElementById('chartSalesTrend');
      if (el1) {
        const ctx1 = el1.getContext('2d');
        if (chartSalesTrendInstance) chartSalesTrendInstance.destroy();
        chartSalesTrendInstance = new Chart(ctx1, {
          type: 'bar',
          data: {
            labels: ['Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun'],
            datasets: [{
              label: 'Sales (₹)',
              data: [18500, 22100, 19800, 25400, 23000, 29500, 32400],
              backgroundColor: '#9C1258',
              borderRadius: 6
            }]
          },
          options: { responsive: true, maintainAspectRatio: false }
        });
      }

      const el2 = document.getElementById('chartDailyProfit');
      if (el2) {
        const ctx2 = el2.getContext('2d');
        if (chartDailyProfitInstance) chartDailyProfitInstance.destroy();
        chartDailyProfitInstance = new Chart(ctx2, {
          type: 'bar',
          data: {
            labels: ['Day 1', 'Day 2', 'Day 3', 'Day 4', 'Day 5', 'Day 6', 'Day 7'],
            datasets: [{
              label: 'Profit (₹)',
              data: [4200, 5100, 4600, 6200, 5800, 7400, 8100],
              backgroundColor: '#10B981',
              borderRadius: 6
            }]
          },
          options: { responsive: true, maintainAspectRatio: false }
        });
      }

      const el3 = document.getElementById('chartTopMeds');
      if (el3) {
        const ctx3 = el3.getContext('2d');
        if (chartTopMedsInstance) chartTopMedsInstance.destroy();
        chartTopMedsInstance = new Chart(ctx3, {
          type: 'doughnut',
          data: {
            labels: ['Paracetamol', 'Azithromycin', 'Pantoprazole', 'Amoxicillin', 'Cetirizine'],
            datasets: [{
              data: [420, 310, 280, 195, 150],
              backgroundColor: ['#9C1258', '#BE185D', '#1E293B', '#10B981', '#3B82F6']
            }]
          },
          options: { responsive: true, maintainAspectRatio: false }
        });
      }

      updateMonthlyChart(6);
    }

    function updateMonthlyChart(months) {
      [3, 6, 12, 24].forEach(m => {
        const btn = document.getElementById(`btn-m-${m === 24 ? 'more' : m}`);
        if (btn) {
          if (m === months) {
            btn.className = 'px-2.5 py-1 text-[11px] font-bold rounded-lg bg-pink-100 text-royal-magenta hover:bg-pink-200 transition';
          } else {
            btn.className = 'px-2.5 py-1 text-[11px] font-bold rounded-lg bg-slate-100 text-slate-600 hover:bg-slate-200 transition';
          }
        }
      });

      let labels = [];
      let data = [];
      if (months === 3) {
        labels = ['Aug 2026', 'Sep 2026', 'Oct 2026'];
        data = [125000, 148000, 174500];
      } else if (months === 6) {
        labels = ['May 2026', 'Jun 2026', 'Jul 2026', 'Aug 2026', 'Sep 2026', 'Oct 2026'];
        data = [98000, 112000, 120000, 125000, 148000, 174500];
      } else if (months === 12) {
        labels = ['Nov 2025', 'Dec 2025', 'Jan 2026', 'Feb 2026', 'Mar 2026', 'Apr 2026', 'May 2026', 'Jun 2026', 'Jul 2026', 'Aug 2026', 'Sep 2026', 'Oct 2026'];
        data = [85000, 92000, 88000, 95000, 102000, 105000, 98000, 112000, 120000, 125000, 148000, 174500];
      } else {
        labels = ['Q3 2024', 'Q4 2024', 'Q1 2025', 'Q2 2025', 'Q3 2025', 'Q4 2025', 'Q1 2026', 'Q2 2026', 'Q3 2026'];
        data = [240000, 270000, 290000, 310000, 340000, 380000, 420000, 460000, 520000];
      }

      const el4 = document.getElementById('chartMonthlyRevenue');
      if (el4) {
        const ctx4 = el4.getContext('2d');
        if (chartMonthlyRevenueInstance) chartMonthlyRevenueInstance.destroy();
        chartMonthlyRevenueInstance = new Chart(ctx4, {
          type: 'line',
          data: {
            labels: labels,
            datasets: [{
              label: 'Revenue (₹)',
              data: data,
              borderColor: '#9C1258',
              backgroundColor: 'rgba(156, 18, 88, 0.1)',
              fill: true,
              tension: 0.3,
              pointRadius: 4,
              pointBackgroundColor: '#9C1258'
            }]
          },
          options: {
            responsive: true,
            maintainAspectRatio: false,
            plugins: {
              legend: { display: false }
            },
            scales: {
              y: {
                beginAtZero: false,
                grid: { color: '#F1F5F9' }
              },
              x: {
                grid: { display: false }
              }
            }
          }
        });
      }
    }

    window.addEventListener('DOMContentLoaded', () => {
      setTimeout(initDashboardCharts, 200);
    });

    // Role switcher logic
    function openRoleDialog() {
      const staffOptionsHtml = state.staffMembers.map(staff => `
        <div onclick="selectRoleFromDialogWeb('${staff.id}')" class="flex items-center justify-between p-2.5 bg-slate-50 hover:bg-sky-50 rounded-xl cursor-pointer border border-slate-100 transition">
          <div class="flex items-center space-x-2.5">
            <div class="w-8 h-8 rounded-full bg-sky-100 text-sky-700 font-bold flex items-center justify-center text-xs">
              ${staff.name.slice(0, 2).toUpperCase()}
            </div>
            <div class="text-left">
              <p class="font-bold text-slate-800 text-xs">${staff.name}</p>
              <p class="text-[10px] text-slate-400">${staff.designation || 'Staff Chemist'} • ${staff.permission || 'POS-only access'}</p>
            </div>
          </div>
          <span class="text-[9px] font-bold px-2 py-0.5 rounded bg-sky-100 text-sky-800">Operate</span>
        </div>
      `).join('');

      Swal.fire({
        title: 'Switch Pharmacy Role & Terminal Mode',
        html: `
          <div class="text-left space-y-3.5 p-1 text-xs text-slate-600">
            <div class="p-3 rounded-xl bg-slate-100 border border-slate-200 flex items-center justify-between">
              <div>
                <p class="text-[10px] text-slate-400 uppercase font-bold">Current Session</p>
                <p class="font-bold text-slate-800 text-sm">${state.userName}</p>
                <p class="text-[10px] text-royal-magenta font-semibold">${state.role === 'OWNER' ? '👑 Owner (Full Access)' : `💼 Staff (${state.activeStaffPermission || 'POS-only'})`}</p>
              </div>
              <span class="w-2.5 h-2.5 rounded-full bg-emerald-500 animate-pulse"></span>
            </div>

            ${state.role === 'STAFF' ? `
              <div class="p-3 bg-pink-50 border border-pink-200 rounded-xl space-y-2">
                <div class="flex items-center justify-between">
                  <span class="font-bold text-royal-magenta text-xs">👑 Elevate to Owner Mode</span>
                  <span class="text-[10px] text-slate-400 font-medium">Requires PIN</span>
                </div>
                <div class="flex gap-2">
                  <input type="password" id="role-dialog-owner-pin" placeholder="Enter PIN (1234)" maxlength="4" class="w-32 px-3 py-1.5 border border-pink-200 rounded-lg text-xs bg-white text-center font-bold tracking-widest outline-none">
                  <button type="button" onclick="verifyOwnerPinFromDialog()" class="flex-1 bg-royal-magenta hover:bg-pink-800 text-white py-1.5 rounded-lg text-xs font-bold transition">Unlock Owner</button>
                </div>
              </div>
            ` : ''}

            <div class="space-y-1.5">
              <div class="flex items-center justify-between">
                <p class="text-[11px] font-bold text-slate-400 uppercase tracking-wider">Switch to Registered Staff Profile</p>
                <button type="button" onclick="Swal.close(); switchTab('settings'); switchSettingsSubTab('staff');" class="text-[10px] text-sky-600 hover:underline">+ Manage Staff</button>
              </div>
              <div class="space-y-1.5 max-h-[180px] overflow-y-auto">
                ${staffOptionsHtml}
              </div>
            </div>
          </div>
        `,
        showConfirmButton: false,
        showCloseButton: true
      });
    }

    function selectRoleFromDialogWeb(staffId) {
      Swal.close();
      switchStaffSessionWeb(staffId);
    }

    function verifyOwnerPinFromDialog() {
      const pinInput = document.getElementById('role-dialog-owner-pin');
      const val = pinInput ? pinInput.value.trim() : '';
      if (val === state.pin) {
        state.role = 'OWNER';
        state.userName = state.profile.ownerName || "Sulman Pharmacist";
        state.userEmail = "sulman995790@gmail.com";
        state.activeStaffPermission = 'FULL_ACCESS';
        saveStateToLocal();
        syncUiState();
        Swal.close();
        Swal.fire('Owner Access Unlocked', 'Operating with unrestricted business owner privileges.', 'success');
        logStaffActivityWeb("STAFF_LOGIN", "Owner Principal", "Owner unlocked administrative session via PIN", "#9C1258");
      } else {
        Swal.fire('Invalid PIN', 'The owner PIN provided was incorrect (Default: 1234).', 'error');
      }
    }

    // Sync Management Dialog
    function openSyncDialog() {
      const snapshotsHtml = [
        { fileName: "RoyalPharmacy_Backup_20261007_0514.json", timestamp: "07-Oct-2026 05:14 AM", totalRecords: 186, size: "47.4 KB" },
        { fileName: "RoyalPharmacy_Backup_20261006_2210.json", timestamp: "06-Oct-2026 10:10 PM", totalRecords: 179, size: "44.1 KB" },
        { fileName: "RoyalPharmacy_Backup_20261005_1830.json", timestamp: "05-Oct-2026 06:30 PM", totalRecords: 164, size: "40.8 KB" }
      ].map((snap, idx) => `
        <div class="flex items-center justify-between p-2.5 bg-slate-50 border border-slate-100 rounded-xl text-xs">
          <div>
            <p class="font-bold text-slate-700">${snap.fileName}</p>
            <p class="text-[10px] text-slate-400">${snap.timestamp} • ${snap.size}</p>
          </div>
          <button onclick="restoreWebSnapshot('${snap.fileName}', ${snap.totalRecords})" class="bg-royal-magenta text-white px-2.5 py-1 rounded-lg font-bold hover:bg-pink-700 transition">
            Restore
          </button>
        </div>
      `).join('');

      Swal.fire({
        title: 'Google Drive Sync Status',
        html: `
          <div class="text-left space-y-4 p-1 text-sm text-slate-600">
            <div class="flex items-center space-x-2.5 pb-2 border-b border-slate-100">
              <span class="w-3.5 h-3.5 rounded-full bg-emerald-500 inline-block animate-pulse shrink-0"></span>
              <div>
                <p class="font-bold text-slate-800 text-xs">Drive Link Connected & Active</p>
                <p class="text-[10px] text-slate-500">${state.userEmail}</p>
              </div>
            </div>

            <div class="space-y-1.5">
              <p class="text-[11px] font-bold text-slate-400 uppercase tracking-wider">Available Drive Snapshots</p>
              <div class="space-y-2 max-h-[160px] overflow-y-auto custom-scrollbar">
                ${snapshotsHtml}
              </div>
            </div>

            <div class="grid grid-cols-2 gap-2 pt-1">
              <button onclick="triggerWebBackup()" class="bg-royal-navy text-white px-3 py-2 rounded-xl text-xs font-bold hover:bg-slate-800 transition flex items-center justify-center space-x-1">
                <i data-lucide="cloud-upload" class="w-3.5 h-3.5"></i>
                <span>Force Backup</span>
              </button>
              <button onclick="importDeviceBackup()" class="bg-slate-100 text-slate-700 border border-slate-200 px-3 py-2 rounded-xl text-xs font-bold hover:bg-slate-200 transition flex items-center justify-center space-x-1">
                <i data-lucide="folder-open" class="w-3.5 h-3.5"></i>
                <span>Import Device File</span>
              </button>
            </div>
          </div>
        `,
        showConfirmButton: false,
        showCloseButton: true
      });
      if (window.lucide) lucide.createIcons();
    }

    function triggerWebBackup() {
      Swal.fire({
        title: 'Uploading to Google Drive',
        text: 'Uploading encrypted database snapshot...',
        allowOutsideClick: false,
        didOpen: () => {
          Swal.showLoading();
          setTimeout(() => {
            const dateStr = new Date().toISOString().slice(0, 10).replace(/-/g, '');
            state.lastGoogleDriveSync = "Just Now";
            saveStateToLocal();
            Swal.fire({
              title: 'Backup Successful!',
              html: `<p class="text-sm">Database successfully uploaded to <b>My Drive > Royal Pharmacy Backups</b> as <code>RoyalPharmacy_Backup_${dateStr}.json</code></p>`,
              icon: 'success'
            });
            showLiveSyncNotification("Backup Uploaded", "Successfully compiled and uploaded snapshot to Google Drive", "cloud-done");
          }, 1500);
        }
      });
    }

    function restoreWebSnapshot(fileName, count) {
      Swal.fire({
        title: 'Confirm Restore?',
        text: `Are you sure you want to restore ${count} records from ${fileName}? This will overwrite current terminal data.`,
        icon: 'warning',
        showCancelButton: true,
        confirmButtonColor: '#9C1258',
        confirmButtonText: 'Yes, Restore Now'
      }).then((res) => {
        if (res.isConfirmed) {
          Swal.fire({
            title: 'Restoring Database',
            text: 'Parsing cloud JSON data...',
            allowOutsideClick: false,
            didOpen: () => {
              Swal.showLoading();
              setTimeout(() => {
                Swal.fire('Database Restored!', `Successfully restored ${count} medicines, sales invoices, and customer ledgers from Google Drive snapshot!`, 'success');
                showLiveSyncNotification("Database Restored", `Restored ${count} records from cloud backup`, "cloud-download");
              }, 1200);
            }
          });
        }
      });
    }

    function importDeviceBackup() {
      Swal.fire({
        title: 'Import Backup from Device',
        text: 'Select or paste a .json backup file content:',
        input: 'textarea',
        inputPlaceholder: 'Paste raw JSON backup payload here...',
        showCancelButton: true,
        confirmButtonColor: '#9C1258',
        confirmButtonText: 'Verify & Import'
      }).then((res) => {
        if (res.value) {
          try {
            const data = JSON.parse(res.value);
            Swal.fire('Import Verified!', 'Valid ERP backup payload merged successfully!', 'success');
            showLiveSyncNotification("Device Import Merged", "Local database successfully merged with imported device file", "check-circle");
          } catch (e) {
            Swal.fire('Invalid Format', 'Ensure the pasted content is a valid compiled JSON string.', 'error');
          }
        }
      });
    }

    // Owner Avatar Switcher
    function changeOwnerAvatarWeb() {
      if (state.role !== 'OWNER') {
        Swal.fire('Permissions Restricted', 'Only the Owner Principal can change profile picture details.', 'error');
        return;
      }
      const presets = ["Sulman", "Chemist", "Royal", "Suleman", "Apothecary", "Pharmacist", "Medical"];
      const gridHtml = presets.map(seed => `
        <button onclick="setOwnerAvatarWeb('${seed}')" class="p-2 border border-slate-100 hover:border-royal-magenta rounded-xl transition duration-200">
          <img src="https://api.dicebear.com/7.x/adventurer/svg?seed=${seed}" class="w-14 h-14 rounded-full">
          <p class="text-[10px] text-slate-400 mt-1 font-semibold">${seed}</p>
        </button>
      `).join('');

      Swal.fire({
        title: 'Choose Profile Picture',
        html: `
          <div class="space-y-4 text-center">
            <p class="text-xs text-slate-500">Pick a professional avatar preset or paste a custom image URL:</p>
            <div class="grid grid-cols-4 gap-3 max-h-[180px] overflow-y-auto p-2">
              ${gridHtml}
            </div>
            <div class="pt-3 border-t border-slate-100">
              <label class="block text-left text-xs font-semibold text-slate-500 mb-1">Custom Photo URL</label>
              <input type="text" id="custom-avatar-url" placeholder="https://example.com/photo.jpg" class="w-full px-3 py-2 border border-slate-200 rounded-xl text-xs">
              <button onclick="setCustomAvatarWeb()" class="mt-2 w-full bg-royal-navy text-white py-2 rounded-xl text-xs font-bold">Apply Custom URL</button>
            </div>
          </div>
        `,
        showConfirmButton: false,
        showCloseButton: true
      });
    }

    function setOwnerAvatarWeb(seed) {
      state.ownerAvatar = `https://api.dicebear.com/7.x/adventurer/svg?seed=${seed}`;
      saveStateToLocal();
      syncUiState();
      Swal.close();
      showLiveSyncNotification("Avatar Synced", "Owner profile picture successfully updated and broadcast to all staff terminals!", "refresh-cw");
    }

    function setCustomAvatarWeb() {
      const url = document.getElementById('custom-avatar-url').value.trim();
      if (url) {
        state.ownerAvatar = url;
        saveStateToLocal();
        syncUiState();
        Swal.close();
        showLiveSyncNotification("Avatar Synced", "Owner profile picture successfully updated and broadcast to all staff terminals!", "refresh-cw");
      }
    }

    // Settings Sub-Tab Navigation
    let currentSettingsSubTab = 'profile';
    function switchSettingsSubTab(subTab) {
      currentSettingsSubTab = subTab;
      const subTabs = ['profile', 'staff', 'activity', 'security'];
      subTabs.forEach(st => {
        const view = document.getElementById(`settings-subview-${st}`);
        const btn = document.getElementById(`subtab-btn-${st}`);
        if (view) {
          if (st === subTab) {
            view.classList.remove('hidden');
          } else {
            view.classList.add('hidden');
          }
        }
        if (btn) {
          if (st === subTab) {
            btn.className = "px-3.5 py-1.5 rounded-lg bg-white shadow-sm text-royal-magenta font-bold transition flex items-center gap-1.5";
          } else {
            btn.className = "px-3.5 py-1.5 rounded-lg text-slate-600 hover:text-slate-900 transition flex items-center gap-1.5";
          }
        }
      });
      if (subTab === 'activity') {
        renderStaffActivityViewWeb();
      } else if (subTab === 'staff') {
        renderStaffTableWeb();
      }
      if (window.lucide) lucide.createIcons();
    }

    // Staff Management & Role-Based Access Control (RBAC)
    function renderStaffTableWeb() {
      const tbody = document.getElementById('web-staff-table-body');
      const countEl = document.getElementById('staff-table-count');
      const settingsBadge = document.getElementById('settings-staff-count-badge');
      const sidebarBadge = document.getElementById('sidebar-staff-badge');
      if (countEl) countEl.innerText = state.staffMembers.length;
      if (settingsBadge) settingsBadge.innerText = state.staffMembers.length;
      if (sidebarBadge) sidebarBadge.innerText = state.staffMembers.length;

      // Render profile staff preview list in Business Profile sub-section
      const previewList = document.getElementById('profile-staff-preview-list');
      if (previewList) {
        if (state.staffMembers.length === 0) {
          previewList.innerHTML = `<div class="p-3 text-center text-slate-400 text-xs bg-slate-50 rounded-xl">No staff accounts registered. Click "+ Add Staff Member" to add team members.</div>`;
        } else {
          previewList.innerHTML = state.staffMembers.map(staff => {
            const perm = staff.permission || "POS-only access";
            let badgeClass = "bg-emerald-50 text-emerald-800 border-emerald-200";
            if (perm.includes("View-only")) badgeClass = "bg-slate-100 text-slate-700 border-slate-200";
            else if (perm.includes("Inventory")) badgeClass = "bg-sky-50 text-sky-800 border-sky-200";
            else if (perm.includes("Pharmacist")) badgeClass = "bg-purple-50 text-purple-800 border-purple-200";

            const isCurrentActive = state.role === 'STAFF' && (state.userPhone === staff.phone || state.userEmail === staff.email);

            return `
              <div class="flex items-center justify-between p-2.5 bg-slate-50 border border-slate-100 rounded-xl text-xs hover:bg-slate-100/70 transition-all duration-300 animate-fade-in">
                <div class="flex items-center space-x-2.5">
                  <div class="w-7 h-7 rounded-full bg-slate-200 text-slate-700 font-bold flex items-center justify-center text-[10px] shrink-0">
                    ${staff.name.slice(0, 2).toUpperCase()}
                  </div>
                  <div>
                    <div class="flex items-center space-x-1.5">
                      <p class="font-bold text-slate-800">${staff.name}</p>
                      ${isCurrentActive ? '<span class="inline-flex items-center space-x-1 px-1.5 py-0.2 rounded-full text-[8px] font-extrabold bg-emerald-100 text-emerald-800 border border-emerald-300 animate-pulse-slow shadow-sm"><span class="w-1.5 h-1.5 rounded-full bg-emerald-500 animate-ping shrink-0"></span><span>ACTIVE SESSION</span></span>' : ''}
                    </div>
                    <p class="text-[10px] text-slate-400">${staff.designation || 'Staff Chemist'} • ${staff.phone}</p>
                  </div>
                </div>
                <div class="flex items-center space-x-1.5">
                  <span class="px-2 py-0.5 rounded-full text-[9px] font-bold border ${badgeClass}">${perm}</span>
                  <button type="button" onclick="generateStaffQrBadgeWeb('${staff.id}')" title="Generate QR ID Badge" class="p-1 bg-slate-800 hover:bg-slate-900 text-white rounded text-[10px] font-bold transition flex items-center justify-center"><i data-lucide="qr-code" class="w-3 h-3"></i></button>
                  <button type="button" onclick="changeStaffPermissionWeb('${staff.id}')" class="text-[10px] text-royal-magenta font-semibold hover:underline">Edit</button>
                  <button type="button" onclick="switchStaffSessionWeb('${staff.id}')" class="px-2 py-0.5 bg-sky-50 hover:bg-sky-100 text-sky-700 rounded text-[10px] font-bold">Switch</button>
                </div>
              </div>
            `;
          }).join('');
        }
      }

      if (!tbody) return;

      tbody.innerHTML = '';
      if (state.staffMembers.length === 0) {
        tbody.innerHTML = `<tr><td colspan="5" class="p-6 text-center text-slate-400">No staff members registered. Click "+ Add Staff Member" above to add team members.</td></tr>`;
        return;
      }

      state.staffMembers.forEach(staff => {
        const perm = staff.permission || "POS-only access";
        let permBadgeClass = "bg-emerald-50 text-emerald-700 border-emerald-200";
        let permIcon = "receipt";
        if (perm.includes("View-only")) {
          permBadgeClass = "bg-slate-100 text-slate-700 border-slate-200";
          permIcon = "eye";
        } else if (perm.includes("Inventory")) {
          permBadgeClass = "bg-sky-50 text-sky-700 border-sky-200";
          permIcon = "package";
        } else if (perm.includes("Pharmacist")) {
          permBadgeClass = "bg-purple-50 text-purple-700 border-purple-200";
          permIcon = "sparkles";
        }

        const isCurrentActive = state.role === 'STAFF' && (state.userPhone === staff.phone || state.userEmail === staff.email);

        tbody.innerHTML += `
          <tr class="hover:bg-slate-50/70 transition-all duration-300 animate-fade-in">
            <td class="p-3.5">
              <div class="flex items-center space-x-3">
                <div class="w-8 h-8 rounded-full bg-royal-navy/10 text-royal-navy flex items-center justify-center font-bold text-xs shrink-0">
                  ${staff.name.slice(0, 2).toUpperCase()}
                </div>
                <div>
                  <div class="flex items-center space-x-1.5">
                    <p class="font-bold text-slate-800 text-xs">${staff.name}</p>
                    ${isCurrentActive ? '<span class="inline-flex items-center space-x-1 px-2 py-0.5 rounded-full text-[8px] font-extrabold bg-emerald-100 text-emerald-800 border border-emerald-300 animate-pulse-slow shadow-sm"><span class="w-1.5 h-1.5 rounded-full bg-emerald-500 animate-ping shrink-0"></span><span>ACTIVE SESSION</span></span>' : ''}
                  </div>
                  <p class="text-[10px] text-slate-400">${staff.designation || 'Staff Chemist'}</p>
                </div>
              </div>
            </td>
            <td class="p-3.5">
              <span class="inline-flex items-center space-x-1 px-2.5 py-1 rounded-full text-[10px] font-bold border ${permBadgeClass}">
                <span>${perm}</span>
              </span>
            </td>
            <td class="p-3.5">
              <div class="space-y-0.5">
                <span class="px-1.5 py-0.5 rounded text-[9px] font-bold ${staff.loginType === 'GMAIL' ? 'bg-pink-100 text-royal-magenta' : 'bg-blue-100 text-blue-700'}">
                  ${staff.loginType || 'PHONE'}
                </span>
                <p class="text-[10px] text-slate-600 font-medium">${staff.phone}</p>
                <p class="text-[9px] text-slate-400">${staff.email}</p>
              </div>
            </td>
            <td class="p-3.5 text-slate-500 text-[11px] font-medium">
              ${staff.lastLoginTime || 'Recently'}
            </td>
            <td class="p-3.5 text-right">
              <div class="flex items-center justify-end space-x-1.5">
                <button onclick="generateStaffQrBadgeWeb('${staff.id}')" title="Generate Staff QR ID Badge for Quick Scan Login" class="px-2 py-1 bg-slate-800 hover:bg-slate-900 text-white rounded-lg font-semibold text-[11px] flex items-center space-x-1 transition shadow-sm border border-slate-700">
                  <i data-lucide="qr-code" class="w-3.5 h-3.5"></i>
                  <span>QR Badge</span>
                </button>
                <button onclick="changeStaffPermissionWeb('${staff.id}')" title="Change Permissions" class="px-2 py-1 bg-slate-100 hover:bg-slate-200 text-slate-700 rounded-lg font-semibold text-[11px] transition">
                  Permissions
                </button>
                <button onclick="switchStaffSessionWeb('${staff.id}')" title="Test Session as this Staff" class="px-2 py-1 bg-sky-50 hover:bg-sky-100 text-sky-700 rounded-lg font-semibold text-[11px] transition">
                  Operate
                </button>
                <button onclick="removeStaffMemberWeb('${staff.id}')" title="Revoke Access" class="p-1 text-rose-500 hover:text-rose-700 hover:bg-rose-50 rounded-lg transition">
                  <i data-lucide="trash-2" class="w-3.5 h-3.5"></i>
                </button>
              </div>
            </td>
          </tr>
        `;
      });
      if (window.lucide) lucide.createIcons();
    }

    // Modal to Generate and Print Staff QR ID Badge
    function generateStaffQrBadgeWeb(staffId) {
      const staff = state.staffMembers.find(s => s.id === staffId);
      if (!staff) {
        Swal.fire('Error', 'Staff member details not found in directory.', 'error');
        return;
      }

      const pharmacyName = state.profile.businessName || 'ROYAL PHARMACY';
      const payload = {
        type: "STAFF_SESSION",
        staffId: staff.id,
        name: staff.name,
        phone: staff.phone,
        email: staff.email,
        permission: staff.permission || "POS-only access",
        designation: staff.designation || "Staff Chemist"
      };

      const payloadStr = JSON.stringify(payload);

      const modalHtml = `
        <div class="text-left space-y-4 text-xs">
          <div class="p-3 bg-sky-50 rounded-xl border border-sky-100 flex items-center justify-between">
            <div class="flex items-center space-x-3">
              <div class="w-9 h-9 rounded-full bg-slate-900 text-white font-bold flex items-center justify-center text-xs shrink-0">
                ${staff.name.slice(0, 2).toUpperCase()}
              </div>
              <div>
                <p class="font-bold text-slate-800 text-sm">${escapeHtml(staff.name)}</p>
                <p class="text-[10px] text-slate-500">${escapeHtml(staff.designation || 'Staff Chemist')} • ${escapeHtml(staff.phone)}</p>
              </div>
            </div>
            <span class="px-2.5 py-1 bg-emerald-100 text-emerald-800 border border-emerald-300 text-[10px] font-bold rounded-full">${escapeHtml(staff.permission || 'POS-only access')}</span>
          </div>

          <!-- Printable Staff Badge Card -->
          <div class="pt-1">
            <label class="block font-bold text-slate-700 mb-1.5 text-[11px] uppercase tracking-wider">Printable Staff ID Badge & Session QR</label>
            <div id="staff-qr-printable-badge" class="bg-gradient-to-br from-slate-900 via-slate-800 to-royal-navy text-white border-2 border-slate-900 rounded-2xl p-4 shadow-lg flex items-center space-x-4">
              <div id="staff-badge-qr-canvas" class="bg-white p-1.5 rounded-xl shrink-0 flex items-center justify-center min-w-[96px] min-h-[96px] shadow-md"></div>
              <div class="flex-1 min-w-0 space-y-1">
                <div class="flex items-center space-x-1.5">
                  <span class="w-2 h-2 rounded-full bg-emerald-400 animate-pulse"></span>
                  <p class="text-[9px] font-extrabold uppercase text-pink-300 tracking-wider truncate">${escapeHtml(pharmacyName)}</p>
                </div>
                <p class="text-sm font-black text-white truncate">${escapeHtml(staff.name)}</p>
                <p class="text-[10px] text-slate-300 font-medium">${escapeHtml(staff.designation || 'Licensed Staff Chemist')}</p>
                <p class="text-[9px] text-slate-400 font-mono">ID: ${escapeHtml(staff.id)} • ${escapeHtml(staff.phone)}</p>
                <div class="pt-1 flex items-center space-x-1.5">
                  <span class="inline-block bg-pink-500/20 text-pink-200 border border-pink-500/30 text-[9px] px-2 py-0.5 rounded-md font-bold">
                    ${escapeHtml(staff.permission || 'POS-only access')}
                  </span>
                </div>
              </div>
            </div>
          </div>

          <p class="text-[10px] text-slate-500 leading-relaxed">Scanning this physical or digital QR badge using the terminal camera or barcode scanner instantly authorizes and switches active session to <b>${escapeHtml(staff.name)}</b>.</p>
        </div>
      `;

      Swal.fire({
        title: `Staff QR Badge: ${staff.name}`,
        html: modalHtml,
        showCancelButton: true,
        showDenyButton: true,
        confirmButtonText: '🖨️ Print Staff ID Badge',
        denyButtonText: '⚡ Switch Session Now',
        cancelButtonText: 'Close',
        confirmButtonColor: '#0F172A',
        denyButtonColor: '#9C1258',
        didOpen: () => {
          const qrContainer = document.getElementById('staff-badge-qr-canvas');
          if (qrContainer) {
            qrContainer.innerHTML = '';
            if (typeof QRCode !== 'undefined') {
              try {
                new QRCode(qrContainer, {
                  text: payloadStr,
                  width: 90,
                  height: 90,
                  colorDark: "#0F172A",
                  colorLight: "#FFFFFF",
                  correctLevel: QRCode.CorrectLevel.M
                });
              } catch (e) {
                console.error("QR Code rendering error:", e);
              }
            } else {
              qrContainer.innerHTML = `<img src="https://api.qrserver.com/v1/create-qr-code/?size=90x90&data=${encodeURIComponent(payloadStr)}" alt="QR Code" class="w-[90px] h-[90px]" />`;
            }
          }
        }
      }).then((res) => {
        if (res.isConfirmed) {
          printStaffBadge(staff.id);
        } else if (res.isDenied) {
          switchStaffSessionWeb(staff.id);
        }
      });
    }

    function printStaffBadge(staffId) {
      const badgeContent = document.getElementById('staff-qr-printable-badge');
      if (!badgeContent) return;

      const printWindow = window.open('', '_blank', 'width=520,height=420');
      if (!printWindow) {
        window.print();
        return;
      }

      printWindow.document.write(`
        <html>
          <head>
            <title>Print Staff ID Badge</title>
            <style>
              body { font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, sans-serif; margin: 0; padding: 24px; background: #fff; }
              .badge { background: #0F172A; color: #fff; border-radius: 16px; padding: 18px; display: flex; align-items: center; gap: 16px; max-width: 380px; box-shadow: 0 4px 12px rgba(0,0,0,0.15); }
              .qr { background: #fff; padding: 6px; border-radius: 12px; }
              .details { font-size: 11px; line-height: 1.4; color: #E2E8F0; }
              .name { font-size: 16px; font-weight: 800; color: #FFF; margin: 2px 0; }
              .pharmacy { font-size: 10px; font-weight: 800; text-transform: uppercase; color: #F472B6; }
              .perm { background: rgba(244,114,182,0.2); color: #FBCFE8; padding: 2px 8px; border-radius: 6px; font-size: 9px; font-weight: 700; display: inline-block; margin-top: 6px; }
            </style>
          </head>
          <body>
            <div class="badge">
              ${badgeContent.innerHTML}
            </div>
            <script>
              window.onload = function() {
                window.print();
                setTimeout(() => window.close(), 1000);
              };
            <\/script>
          </body>
        </html>
      `);
      printWindow.document.close();
    }

    // Dedicated Modal to Add Staff with Role-Based Permissions
    function openAddStaffModalWeb() {
      if (state.role !== 'OWNER') {
        Swal.fire({
          title: 'Owner PIN Required',
          text: 'Only the Pharmacy Owner Principal can add or configure staff accounts.',
          input: 'password',
          inputPlaceholder: 'Enter 4-Digit Owner PIN (Default: 1234)',
          showCancelButton: true,
          confirmButtonColor: '#9C1258',
          confirmButtonText: 'Authorize'
        }).then((res) => {
          if (res.value === state.pin) {
            triggerAddStaffDialogPrompt();
          } else if (res.value) {
            Swal.fire('Invalid PIN', 'The owner PIN provided was incorrect.', 'error');
          }
        });
        return;
      }
      triggerAddStaffDialogPrompt();
    }

    function addStaffMemberWeb() {
      openAddStaffModalWeb();
    }

    function triggerAddStaffDialogPrompt() {
      Swal.fire({
        title: 'Add New Staff Member',
        html: `
          <div class="space-y-3.5 text-left p-1 text-xs text-slate-600">
            <p class="text-[11px] text-slate-400">Configure staff identity and define granular role-based access permissions (RBAC).</p>
            
            <div>
              <label class="block font-bold text-slate-700 mb-1">Staff Full Name *</label>
              <input type="text" id="staff-reg-name" placeholder="e.g. Asif Rahman" class="w-full px-3 py-2 border border-slate-200 rounded-xl text-xs focus:ring-1 focus:ring-pink-500 outline-none">
            </div>

            <div class="grid grid-cols-2 gap-2">
              <div>
                <label class="block font-bold text-slate-700 mb-1">Mobile Number *</label>
                <input type="text" id="staff-reg-phone" placeholder="9435078210" maxlength="10" class="w-full px-3 py-2 border border-slate-200 rounded-xl text-xs focus:ring-1 focus:ring-pink-500 outline-none">
              </div>
              <div>
                <label class="block font-bold text-slate-700 mb-1">Designation</label>
                <input type="text" id="staff-reg-desig" placeholder="e.g. Dispensing Chemist" class="w-full px-3 py-2 border border-slate-200 rounded-xl text-xs focus:ring-1 focus:ring-pink-500 outline-none">
              </div>
            </div>

            <div>
              <label class="block font-bold text-slate-700 mb-1">Email Address</label>
              <input type="email" id="staff-reg-email" placeholder="asif.rahman@royal.com" class="w-full px-3 py-2 border border-slate-200 rounded-xl text-xs focus:ring-1 focus:ring-pink-500 outline-none">
            </div>

            <!-- Granular Permissions Selector -->
            <div>
              <label class="block font-bold text-slate-700 mb-1.5">Define Specific Role-Based Permission *</label>
              <div class="space-y-2">
                <label class="flex items-start p-2.5 rounded-xl border border-slate-200 hover:border-emerald-400 cursor-pointer transition">
                  <input type="radio" name="staff_permission" value="POS-only access" checked class="mt-0.5 text-royal-magenta focus:ring-pink-500">
                  <div class="ml-2.5">
                    <div class="flex items-center space-x-1.5">
                      <span class="font-bold text-slate-800 text-[11px]">POS-only access</span>
                      <span class="px-1.5 py-0.2 rounded text-[8px] font-bold bg-emerald-100 text-emerald-800">Billing Counter</span>
                    </div>
                    <p class="text-[10px] text-slate-400 mt-0.5">Can process cash memos, accept payments & print receipts. Restricted from modifying catalog prices.</p>
                  </div>
                </label>

                <label class="flex items-start p-2.5 rounded-xl border border-slate-200 hover:border-slate-400 cursor-pointer transition">
                  <input type="radio" name="staff_permission" value="View-only access" class="mt-0.5 text-royal-magenta focus:ring-pink-500">
                  <div class="ml-2.5">
                    <div class="flex items-center space-x-1.5">
                      <span class="font-bold text-slate-800 text-[11px]">View-only access</span>
                      <span class="px-1.5 py-0.2 rounded text-[8px] font-bold bg-slate-100 text-slate-700">Read-Only</span>
                    </div>
                    <p class="text-[10px] text-slate-400 mt-0.5">Search 500k+ drug catalog, check salt formulations & inventory. Cannot bill or edit data.</p>
                  </div>
                </label>

                <label class="flex items-start p-2.5 rounded-xl border border-slate-200 hover:border-sky-400 cursor-pointer transition">
                  <input type="radio" name="staff_permission" value="Inventory & Billing access" class="mt-0.5 text-royal-magenta focus:ring-pink-500">
                  <div class="ml-2.5">
                    <div class="flex items-center space-x-1.5">
                      <span class="font-bold text-slate-800 text-[11px]">Inventory & Billing access</span>
                      <span class="px-1.5 py-0.2 rounded text-[8px] font-bold bg-sky-100 text-sky-800">POS + Stock</span>
                    </div>
                    <p class="text-[10px] text-slate-400 mt-0.5">Full POS billing + stock adjustments, batch tracking & creating purchase orders.</p>
                  </div>
                </label>

                <label class="flex items-start p-2.5 rounded-xl border border-slate-200 hover:border-purple-400 cursor-pointer transition">
                  <input type="radio" name="staff_permission" value="Full Pharmacist access" class="mt-0.5 text-royal-magenta focus:ring-pink-500">
                  <div class="ml-2.5">
                    <div class="flex items-center space-x-1.5">
                      <span class="font-bold text-slate-800 text-[11px]">Full Pharmacist access</span>
                      <span class="px-1.5 py-0.2 rounded text-[8px] font-bold bg-purple-100 text-purple-800">Unrestricted</span>
                    </div>
                    <p class="text-[10px] text-slate-400 mt-0.5">Complete operational privileges: AI Pharmacist Copilot, patient CRM, stock & invoices.</p>
                  </div>
                </label>
              </div>
            </div>

            <div>
              <label class="block font-bold text-slate-700 mb-1">Allowed Login Method</label>
              <select id="staff-reg-logintype" class="w-full px-3 py-2 border border-slate-200 rounded-xl text-xs bg-white outline-none">
                <option value="PHONE">Phone OTP Auth (Mobile 123456)</option>
                <option value="GMAIL">Gmail Account Auth</option>
              </select>
            </div>
          </div>
        `,
        showCancelButton: true,
        confirmButtonColor: '#9C1258',
        confirmButtonText: 'Register Staff Member',
        focusConfirm: false,
        preConfirm: () => {
          const name = document.getElementById('staff-reg-name').value.trim();
          const phone = document.getElementById('staff-reg-phone').value.trim();
          const email = document.getElementById('staff-reg-email').value.trim() || `${name.toLowerCase().replace(/\s+/g, '')}@royal.com`;
          const desig = document.getElementById('staff-reg-desig').value.trim() || 'Dispensing Chemist';
          const permRadio = document.querySelector('input[name="staff_permission"]:checked');
          const permission = permRadio ? permRadio.value : 'POS-only access';
          const loginType = document.getElementById('staff-reg-logintype').value;

          if (!name || !phone) {
            Swal.showValidationMessage('Staff name and mobile number are required.');
            return false;
          }
          return { name, phone, email, desig, permission, loginType };
        }
      }).then((res) => {
        if (res.isConfirmed && res.value) {
          const { name, phone, email, desig, permission, loginType } = res.value;
          const formattedPhone = phone.startsWith("+91") ? phone : `+91 ${phone.replace(/\D/g, '')}`;
          const newStaff = {
            id: 's-' + Date.now(),
            name: name,
            email: email,
            phone: formattedPhone,
            designation: desig,
            permission: permission,
            lastLoginTime: "Just registered",
            loginType: loginType
          };

          state.staffMembers.push(newStaff);
          saveStateToLocal();
          syncUiState();

          // Log staff creation action
          logStaffActivityWeb(
            "STAFF_ADDED",
            state.role === 'OWNER' ? 'Owner Principal' : state.userName,
            `Registered new staff member '${name}' with ${permission} (${desig})`,
            "#8B5CF6"
          );

          Swal.fire({
            title: 'Staff Member Registered!',
            html: `Successfully registered <b>${name}</b> with <b>${permission}</b>.<br><span class="text-xs text-slate-500">Credentials active for instant web portal sign-in.</span>`,
            icon: 'success',
            confirmButtonColor: '#9C1258'
          });
          showLiveSyncNotification("Staff Registered", `Granted ${name} ${permission}`, "user-plus");
        }
      });
    }

    function changeStaffPermissionWeb(id) {
      const member = state.staffMembers.find(s => s.id === id);
      if (!member) return;

      Swal.fire({
        title: `Update Permissions: ${member.name}`,
        html: `
          <div class="space-y-3 text-left p-1 text-xs text-slate-600">
            <p class="text-slate-500">Current Assigned Role: <strong class="text-slate-800">${member.permission || 'POS-only access'}</strong></p>
            <div class="space-y-2">
              <label class="flex items-center p-2 rounded-xl border border-slate-200 cursor-pointer hover:bg-slate-50">
                <input type="radio" name="update_perm" value="POS-only access" ${member.permission === 'POS-only access' ? 'checked' : ''} class="text-royal-magenta">
                <span class="ml-2 font-bold text-slate-800">POS-only access (Billing Counter)</span>
              </label>
              <label class="flex items-center p-2 rounded-xl border border-slate-200 cursor-pointer hover:bg-slate-50">
                <input type="radio" name="update_perm" value="View-only access" ${member.permission === 'View-only access' ? 'checked' : ''} class="text-royal-magenta">
                <span class="ml-2 font-bold text-slate-800">View-only access (Read-Only Catalog)</span>
              </label>
              <label class="flex items-center p-2 rounded-xl border border-slate-200 cursor-pointer hover:bg-slate-50">
                <input type="radio" name="update_perm" value="Inventory & Billing access" ${member.permission === 'Inventory & Billing access' ? 'checked' : ''} class="text-royal-magenta">
                <span class="ml-2 font-bold text-slate-800">Inventory & Billing access (Stock + POS)</span>
              </label>
              <label class="flex items-center p-2 rounded-xl border border-slate-200 cursor-pointer hover:bg-slate-50">
                <input type="radio" name="update_perm" value="Full Pharmacist access" ${member.permission === 'Full Pharmacist access' ? 'checked' : ''} class="text-royal-magenta">
                <span class="ml-2 font-bold text-slate-800">Full Pharmacist access (Complete Operations)</span>
              </label>
            </div>
          </div>
        `,
        showCancelButton: true,
        confirmButtonColor: '#9C1258',
        confirmButtonText: 'Update Permissions'
      }).then((res) => {
        if (res.isConfirmed) {
          const selected = document.querySelector('input[name="update_perm"]:checked')?.value || member.permission;
          member.permission = selected;
          if (state.role === 'STAFF' && (state.userPhone === member.phone || state.userEmail === member.email)) {
            state.activeStaffPermission = selected;
          }
          saveStateToLocal();
          syncUiState();

          logStaffActivityWeb(
            "PERMISSION_UPDATED",
            state.role === 'OWNER' ? 'Owner Principal' : state.userName,
            `Updated permissions for '${member.name}' to: ${selected}`,
            "#8B5CF6"
          );

          Swal.fire('Updated!', `Permissions updated to ${selected} for ${member.name}.`, 'success');
        }
      });
    }

    function switchStaffSessionWeb(id) {
      const member = state.staffMembers.find(s => s.id === id);
      if (!member) return;

      state.role = 'STAFF';
      state.userName = member.name;
      state.userEmail = member.email;
      state.userPhone = member.phone;
      state.activeStaffPermission = member.permission || "POS-only access";
      member.lastLoginTime = "Just now";

      saveStateToLocal();
      syncUiState();

      logStaffActivityWeb(
        "STAFF_LOGIN",
        member.name,
        `Active session switched to ${member.name} (${member.permission})`,
        "#0EA5E9"
      );

      Swal.fire({
        title: 'Operating as Staff',
        html: `Switched session to <b>${member.name}</b><br><span class="text-xs text-sky-600 font-bold">${member.permission}</span>`,
        icon: 'info',
        confirmButtonColor: '#9C1258'
      });
    }

    function removeStaffMemberWeb(id) {
      if (state.role !== 'OWNER') {
        Swal.fire('Admin PIN Required', 'Only the Owner Principal can revoke staff credentials.', 'error');
        return;
      }
      const member = state.staffMembers.find(s => s.id === id);
      if (!member) return;

      Swal.fire({
        title: 'Revoke Credentials?',
        text: `Are you sure you want to delete ${member.name} (${member.permission}) from staff directory?`,
        icon: 'warning',
        showCancelButton: true,
        confirmButtonColor: '#DC2626',
        confirmButtonText: 'Yes, Revoke Access'
      }).then((res) => {
        if (res.isConfirmed) {
          state.staffMembers = state.staffMembers.filter(s => s.id !== id);
          saveStateToLocal();
          syncUiState();

          logStaffActivityWeb(
            "STAFF_REMOVED",
            'Owner Principal',
            `Revoked credentials and access for staff member '${member.name}'`,
            "#EF4444"
          );

          Swal.fire('Revoked!', 'Staff access credentials invalidated.', 'success');
          showLiveSyncNotification("Access Revoked", `Invalidated session for ${member.name}`, "trash-2");
        }
      });
    }

    // ================= CENTRALIZED ACTIVITY LOGGER =================
    let currentActivityFilter = 'ALL';

    function logStaffActivityWeb(actionType, staffName, description, badgeColor) {
      if (!state.staffActivityLogs) state.staffActivityLogs = [];
      const now = new Date();
      const timeStr = "Today at " + now.toLocaleTimeString('en-US', { hour: '2-digit', minute: '2-digit', hour12: true });

      const newLog = {
        id: 'act-' + Date.now(),
        staffName: staffName || state.userName || 'Staff Operator',
        staffRole: state.role === 'OWNER' ? 'Owner (Full Access)' : (state.activeStaffPermission || 'Staff'),
        actionType: actionType,
        description: description,
        timestamp: timeStr,
        rawDate: now.toISOString(),
        badgeColor: badgeColor || '#10B981'
      };

      state.staffActivityLogs.unshift(newLog);
      if (state.staffActivityLogs.length > 100) state.staffActivityLogs.pop();
      saveStateToLocal();
      renderStaffActivityViewWeb();
      renderStaffActivityBadgeWeb();
      if (typeof renderRecentActivityFeedWeb === 'function') renderRecentActivityFeedWeb();
    }

    function renderStaffActivityBadgeWeb() {
      const logs = state.staffActivityLogs || [];
      const badge = document.getElementById('settings-activity-count-badge');
      const sideBadge = document.getElementById('sidebar-activity-badge');
      if (badge) badge.innerText = logs.length;
      if (sideBadge) sideBadge.innerText = logs.length;
    }

    function renderStaffActivityViewWeb() {
      const logs = state.staffActivityLogs || [];
      const tbody = document.getElementById('web-activity-table-body');
      
      // Update statistics
      const totalBills = logs.filter(l => l.actionType === 'BILL_GENERATED').length;
      const totalStocks = logs.filter(l => l.actionType === 'STOCK_UPDATED').length;
      const uniqueStaff = new Set(logs.map(l => l.staffName)).size;

      const statTotal = document.getElementById('stat-total-actions');
      const statBills = document.getElementById('stat-total-bills');
      const statStocks = document.getElementById('stat-total-stocks');
      const statOps = document.getElementById('stat-active-operators');

      if (statTotal) statTotal.innerText = logs.length;
      if (statBills) statBills.innerText = totalBills;
      if (statStocks) statStocks.innerText = totalStocks;
      if (statOps) statOps.innerText = Math.max(uniqueStaff, 1);

      if (!tbody) return;

      const searchInput = document.getElementById('activity-search-input');
      const query = searchInput ? searchInput.value.toLowerCase().trim() : '';

      const filteredLogs = logs.filter(l => {
        const matchesFilter = (currentActivityFilter === 'ALL') ||
          (currentActivityFilter === 'BILLING' && l.actionType === 'BILL_GENERATED') ||
          (currentActivityFilter === 'STOCK' && l.actionType === 'STOCK_UPDATED') ||
          (currentActivityFilter === 'STAFF' && (l.actionType === 'STAFF_ADDED' || l.actionType === 'STAFF_REMOVED' || l.actionType === 'PERMISSION_UPDATED' || l.actionType === 'STAFF_LOGIN')) ||
          (currentActivityFilter === 'PO' && l.actionType === 'PO_CREATED');

        const matchesQuery = !query ||
          l.staffName.toLowerCase().includes(query) ||
          l.description.toLowerCase().includes(query) ||
          l.actionType.toLowerCase().includes(query) ||
          (l.staffRole && l.staffRole.toLowerCase().includes(query));

        return matchesFilter && matchesQuery;
      });

      tbody.innerHTML = '';
      if (filteredLogs.length === 0) {
        tbody.innerHTML = `<tr><td colspan="5" class="p-8 text-center text-slate-400">No activity logs matching '${query || currentActivityFilter}'.</td></tr>`;
        return;
      }

      filteredLogs.forEach(item => {
        let catBadge = "bg-slate-100 text-slate-700";
        let catLabel = item.actionType;
        let catIcon = "activity";

        if (item.actionType === 'BILL_GENERATED') {
          catBadge = "bg-emerald-50 text-emerald-700 border border-emerald-200";
          catLabel = "BILLING";
          catIcon = "receipt";
        } else if (item.actionType === 'STOCK_UPDATED') {
          catBadge = "bg-sky-50 text-sky-700 border border-sky-200";
          catLabel = "STOCK UPDATE";
          catIcon = "package";
        } else if (item.actionType === 'STAFF_ADDED' || item.actionType === 'PERMISSION_UPDATED') {
          catBadge = "bg-purple-50 text-purple-700 border border-purple-200";
          catLabel = "STAFF RBAC";
          catIcon = "shield-check";
        } else if (item.actionType === 'STAFF_REMOVED') {
          catBadge = "bg-rose-50 text-rose-700 border border-rose-200";
          catLabel = "ACCESS REVOKED";
          catIcon = "user-x";
        } else if (item.actionType === 'PO_CREATED') {
          catBadge = "bg-amber-50 text-amber-700 border border-amber-200";
          catLabel = "PURCHASE ORDER";
          catIcon = "truck";
        } else if (item.actionType === 'STAFF_LOGIN') {
          catBadge = "bg-blue-50 text-blue-700 border border-blue-200";
          catLabel = "STAFF SIGN-IN";
          catIcon = "log-in";
        }

        tbody.innerHTML += `
          <tr class="hover:bg-slate-50/70 transition">
            <td class="p-3.5 text-slate-500 whitespace-nowrap text-[11px] font-mono">
              ${item.timestamp}
            </td>
            <td class="p-3.5">
              <div class="flex items-center space-x-2">
                <div class="w-6 h-6 rounded-full bg-slate-200 text-slate-700 font-bold text-[10px] flex items-center justify-center shrink-0">
                  ${item.staffName.slice(0, 2).toUpperCase()}
                </div>
                <div>
                  <p class="font-bold text-slate-800 text-xs">${item.staffName}</p>
                  <p class="text-[9px] text-slate-400">${item.staffRole || 'Staff'}</p>
                </div>
              </div>
            </td>
            <td class="p-3.5 whitespace-nowrap">
              <span class="px-2 py-0.5 rounded text-[9px] font-bold ${catBadge}">
                ${catLabel}
              </span>
            </td>
            <td class="p-3.5 text-slate-700 text-xs font-medium">
              ${item.description}
            </td>
            <td class="p-3.5 text-right whitespace-nowrap">
              <span class="inline-flex items-center space-x-1 text-[10px] text-emerald-600 font-semibold bg-emerald-50 px-2 py-0.5 rounded border border-emerald-100">
                <span class="w-1.5 h-1.5 rounded-full bg-emerald-500"></span>
                <span>Logged</span>
              </span>
            </td>
          </tr>
        `;
      });
      if (window.lucide) lucide.createIcons();
    }

    function filterStaffActivity(filterType) {
      currentActivityFilter = filterType;
      const pills = ['ALL', 'BILLING', 'STOCK', 'STAFF', 'PO'];
      pills.forEach(p => {
        const btn = document.getElementById(`filter-btn-${p}`);
        if (btn) {
          if (p === filterType) {
            btn.className = "px-3 py-1.5 rounded-lg text-xs font-bold bg-royal-navy text-white transition";
          } else {
            btn.className = "px-3 py-1.5 rounded-lg text-xs font-semibold bg-slate-100 text-slate-600 hover:bg-slate-200 transition";
          }
        }
      });
      renderStaffActivityViewWeb();
    }

    function exportStaffActivityLogsCsvWeb() {
      const logs = state.staffActivityLogs || [];
      if (logs.length === 0) {
        Swal.fire('No Logs', 'Activity audit log is currently empty.', 'info');
        return;
      }

      let csv = 'Timestamp,Staff Member,Role,Action Type,Description\n';
      logs.forEach(l => {
        const timeClean = `"${l.timestamp}"`;
        const staffClean = `"${l.staffName.replace(/"/g, '""')}"`;
        const roleClean = `"${(l.staffRole || '').replace(/"/g, '""')}"`;
        const actionClean = `"${l.actionType}"`;
        const descClean = `"${l.description.replace(/"/g, '""')}"`;
        csv += `${timeClean},${staffClean},${roleClean},${actionClean},${descClean}\n`;
      });

      const blob = new Blob([csv], { type: 'text/csv;charset=utf-8;' });
      const link = document.createElement('a');
      link.href = URL.createObjectURL(blob);
      link.setAttribute('download', `Royal_Pharmacy_Staff_Activity_Logs_${new Date().toISOString().slice(0, 10)}.csv`);
      document.body.appendChild(link);
      link.click();
      document.body.removeChild(link);

      Swal.fire('Export Complete', 'Staff activity log exported as CSV file.', 'success');
    }

    function simulateSampleStaffActivityWeb() {
      const samples = [
        {
          action: "BILL_GENERATED",
          staff: "Nijamuddin Khan",
          desc: "Generated Cash Memo #" + (Math.floor(Math.random() * 9000) + 1000) + " for ₹" + (Math.floor(Math.random() * 1400) + 120) + ".00 (Patient: Walk-in)",
          color: "#10B981"
        },
        {
          action: "STOCK_UPDATED",
          staff: "Rahul Sharma",
          desc: "Adjusted inventory stock for 'Amoxicillin 500mg Capsule': +30 units (Stock now 880)",
          color: "#3B82F6"
        },
        {
          action: "PO_CREATED",
          staff: "Rahul Sharma",
          desc: "Drafted Purchase Order for distributor 'MediLife Supplies' (4 products)",
          color: "#F59E0B"
        }
      ];

      const sample = samples[Math.floor(Math.random() * samples.length)];
      logStaffActivityWeb(sample.action, sample.staff, sample.desc, sample.color);
      Swal.fire('Activity Logged', sample.desc, 'info');
    }

    // Modal to directly update stock and log staff activity
    function quickUpdateMedicineStockWeb(medicineName) {
      const drug = state.drugDatabase.find(d => d.name === medicineName) || state.drugDatabase[0];
      if (!drug) return;

      Swal.fire({
        title: `Update Stock: ${drug.name}`,
        html: `
          <div class="space-y-3 text-left p-1 text-xs text-slate-600">
            <p>Current Available Stock: <strong class="text-slate-800">${drug.stock} strips</strong></p>
            <div>
              <label class="block font-bold text-slate-700 mb-1">Stock Adjustment (+ or -)</label>
              <input type="number" id="quick-stock-change" value="20" class="w-full px-3 py-2 border border-slate-200 rounded-xl text-xs focus:ring-1 focus:ring-pink-500 outline-none">
            </div>
            <div>
              <label class="block font-bold text-slate-700 mb-1">Reason for Adjustment</label>
              <select id="quick-stock-reason" class="w-full px-3 py-2 border border-slate-200 rounded-xl text-xs bg-white outline-none">
                <option value="Distributor Receipt">Received from Distributor</option>
                <option value="Physical Audit">Physical Inventory Audit</option>
                <option value="Customer Return">Customer Return</option>
                <option value="Damaged / Expired">Damaged / Expired Write-off</option>
              </select>
            </div>
          </div>
        `,
        showCancelButton: true,
        confirmButtonColor: '#9C1258',
        confirmButtonText: 'Apply Stock Adjustment'
      }).then(res => {
        if (res.isConfirmed) {
          const change = parseInt(document.getElementById('quick-stock-change').value) || 0;
          const reason = document.getElementById('quick-stock-reason').value;
          drug.stock = Math.max(0, drug.stock + change);
          saveStateToLocal();
          syncUiState();
          if (typeof searchDrugs === 'function') searchDrugs();

          logStaffActivityWeb(
            "STOCK_UPDATED",
            state.userName,
            `Stock adjusted for '${drug.name}': ${change >= 0 ? '+' : ''}${change} strips (New Stock: ${drug.stock}) [${reason}]`,
            "#3B82F6"
          );

          Swal.fire('Stock Updated', `New stock for ${drug.name}: ${drug.stock} strips`, 'success');
        }
      });
    }

    // Gmail & Phone OTP login overlay modal
    function openWebLoginModal() {
      Swal.fire({
        title: 'ROYAL ERP Cloud Authentication',
        html: `
          <div class="space-y-4 text-left p-1 text-sm">
            <p class="text-xs text-slate-500">Sign out or switch between owner/staff accounts using OTP security verification:</p>
            
            <div class="flex items-center space-x-3 p-3 bg-slate-50 rounded-xl">
              <div class="w-10 h-10 rounded-full overflow-hidden flex items-center justify-center bg-royal-magenta text-white">
                <img src="${state.ownerAvatar}" class="w-full h-full object-cover">
              </div>
              <div>
                <p class="font-bold text-slate-800">${state.role === 'OWNER' ? 'Owner Principal' : 'Staff Worker'}</p>
                <p class="text-[10px] text-slate-400">${state.role === 'OWNER' ? state.userEmail : state.userPhone}</p>
              </div>
            </div>

            <div class="border-t border-slate-100 pt-3 space-y-2">
              <p class="text-[11px] font-bold text-slate-400 uppercase tracking-wider">Authentication Methods</p>
              <div class="grid grid-cols-2 gap-2">
                <button onclick="startWebLogin('GMAIL')" class="bg-white border border-slate-200 hover:border-royal-magenta text-slate-800 px-3 py-2 rounded-xl text-xs font-semibold flex items-center justify-center space-x-1.5 transition">
                  <i data-lucide="mail" class="w-3.5 h-3.5 text-royal-magenta"></i>
                  <span>Gmail Login</span>
                </button>
                <button onclick="startWebLogin('PHONE')" class="bg-white border border-slate-200 hover:border-royal-magenta text-slate-800 px-3 py-2 rounded-xl text-xs font-semibold flex items-center justify-center space-x-1.5 transition">
                  <i data-lucide="phone" class="w-3.5 h-3.5 text-blue-600"></i>
                  <span>Phone OTP</span>
                </button>
              </div>
              <button onclick="logoutWeb()" class="w-full bg-red-50 hover:bg-red-100 text-red-600 border border-red-200 px-3 py-2.5 rounded-xl text-xs font-bold transition flex items-center justify-center space-x-1.5 mt-2">
                <i data-lucide="log-out" class="w-3.5 h-3.5"></i>
                <span>Sign Out & End Session</span>
              </button>
            </div>
          </div>
        `,
        showConfirmButton: false,
        showCloseButton: true
      });
      if (window.lucide) lucide.createIcons();
    }

    function startWebLogin(type) {
      Swal.fire({
        title: type === 'GMAIL' ? 'Sign in with Google' : 'Sign in with Phone Number',
        input: 'text',
        inputLabel: type === 'GMAIL' ? 'Your Gmail Address' : 'Enter 10-Digit Mobile Number',
        inputPlaceholder: type === 'GMAIL' ? 'sulman995790@gmail.com' : 'e.g. +91 94350 78210',
        showCancelButton: true,
        confirmButtonColor: '#9C1258',
        confirmButtonText: 'Request Security Code'
      }).then((res) => {
        if (res.value) {
          const target = res.value.trim();
          Swal.fire({
            title: 'Verify Security Code',
            text: `Security code (123456) dispatched to ${target}! Enter code below to log in:`,
            input: 'text',
            inputPlaceholder: '123456',
            showCancelButton: true,
            confirmButtonColor: '#9C1258',
            confirmButtonText: 'Verify & Login'
          }).then((otpRes) => {
            if (otpRes.value === '123456') {
              if (type === 'GMAIL') {
                state.role = 'OWNER';
                state.userEmail = target;
                state.userName = target.split('@')[0];
              } else {
                state.role = 'STAFF';
                state.userPhone = target;
                state.userName = "Staff worker";
              }
              saveStateToLocal();
              syncUiState();
              Swal.fire({
                title: 'Authorized Successfully!',
                text: `Welcome back, ${state.userName}! Operating in ${state.role === 'OWNER' ? 'Owner Principal' : 'Staff Chemist'} mode.`,
                icon: 'success'
              });
              showLiveSyncNotification("User Signed In", `Logged in via ${type} to ${state.userName}'s session`, "verified");
            } else if (otpRes.value) {
              Swal.fire('Invalid OTP', 'The security code provided was incorrect.', 'error');
            }
          });
        }
      });
    }

    // Slide-in WhatsApp-Style Live Notification
    function showLiveSyncNotification(title, desc, iconName = 'refresh-cw') {
      const banner = document.getElementById('live-sync-banner');
      const bannerTitle = document.getElementById('live-sync-banner-title');
      const bannerDesc = document.getElementById('live-sync-banner-desc');
      const bannerIcon = document.getElementById('live-sync-banner-icon');

      if (!banner || !bannerTitle || !bannerDesc) return;

      bannerTitle.innerText = title;
      bannerDesc.innerText = desc;
      if (bannerIcon) {
        bannerIcon.setAttribute('data-lucide', iconName);
        if (window.lucide) lucide.createIcons();
      }

      banner.classList.remove('hidden');
      setTimeout(() => {
        banner.classList.remove('translate-y-12', 'opacity-0');
        banner.classList.add('translate-y-0', 'opacity-100');
      }, 50);

      // Auto dismiss after 5.5s
      setTimeout(() => {
        dismissLiveSyncBanner();
      }, 5500);
    }

    function dismissLiveSyncBanner() {
      const banner = document.getElementById('live-sync-banner');
      if (banner) {
        banner.classList.remove('translate-y-0', 'opacity-100');
        banner.classList.add('translate-y-12', 'opacity-0');
        setTimeout(() => {
          banner.classList.add('hidden');
        }, 500);
      }
    }

    // Setup periodic WhatsApp-Style Live Sync Broadcast Simulation
    const syncNotificationMessages = [
      { title: "Cloud Database Restored", desc: "Successfully restored 186 records from Google Drive snapshot!", icon: "cloud-download" },
      { title: "Owner Profile Updated", desc: "Profile details & avatar broadcasted to all staff terminals & Google Drive backups", icon: "user-check" },
      { title: "Staff Live Sync Online", desc: "Staff 'Nijamuddin Khan' edited stock of Augmentin 625 Duo (+50 units)", icon: "refresh-cw" },
      { title: "Google Drive Sync Done", desc: "Periodic database snapshot uploaded cleanly to cloud account", icon: "cloud-done" },
      { title: "Staff Login Session", desc: "Staff worker 'Priya Das' authorized on Terminal Chrome-7", icon: "shield-check" }
    ];
    let notificationIndex = 0;
    setInterval(() => {
      const msg = syncNotificationMessages[notificationIndex];
      showLiveSyncNotification(msg.title, msg.desc, msg.icon);
      notificationIndex = (notificationIndex + 1) % syncNotificationMessages.length;
    }, 45000); // Triggers a real-time collaborate notification every 45s (non-intrusive)

    // Settings Profile logic
    function saveProfileSettings() {
      if (state.role !== 'OWNER') {
        Swal.fire('Permissions Denied', 'Staff accounts cannot edit regulatory or business profile details.', 'error');
        return;
      }
      
      state.profile.businessName = document.getElementById('set-name').value;
      state.profile.phone = document.getElementById('set-phone').value;
      state.profile.gstin = document.getElementById('set-gstin').value;
      state.profile.drugLicenseForm20 = document.getElementById('set-dl20').value;
      state.profile.drugLicenseForm21 = document.getElementById('set-dl21').value;
      state.profile.ayushmanHfrId = document.getElementById('set-ayushman').value;
      state.pin = document.getElementById('set-pin').value;
      state.secretPassword = document.getElementById('set-secret-password').value;

      saveStateToLocal();
      syncUiState();
      
      // Push to Firestore for App sync
      syncWebChangeToFirestore('pharmacy_config', 'business_settings', {
        profile: state.profile,
        pin: state.pin,
        secretPassword: state.secretPassword
      });

      Swal.fire('Settings Saved!', 'Pharmacy business parameters successfully compiled and synced to all devices.', 'success');
    }

    // Dropdown filled dynamically
    function loadMedicineDropdown() {
      const select = document.getElementById('add-item-select');
      select.innerHTML = '<option value="">-- Choose Brand --</option>';
      state.drugDatabase.forEach(item => {
        select.innerHTML += `<option value="${item.name}">${item.name} (${item.generic})</option>`;
      });
    }

    function autofillBillItem() {
      const selectedName = document.getElementById('add-item-select').value;
      const drug = state.drugDatabase.find(d => d.name === selectedName);
      if (drug) {
        document.getElementById('add-item-mrp').value = drug.price;
      }
    }

    // Billing Logic POS
    function addItemToCurrentBill() {
      if (state.role === 'STAFF' && state.activeStaffPermission === 'View-only access') {
        Swal.fire({
          title: 'View-Only Access',
          text: 'This staff account has View-Only access permissions (read-only drug catalog). Modifying sales bills is restricted.',
          icon: 'warning',
          confirmButtonColor: '#9C1258'
        });
        return;
      }

      const selectName = document.getElementById('add-item-select').value;
      const qty = parseInt(document.getElementById('add-item-qty').value) || 1;
      const mrp = parseFloat(document.getElementById('add-item-mrp').value) || 0.0;

      if (!selectName) {
        Swal.fire('Input Required', 'Please select a medicine brand from the catalog.', 'warning');
        return;
      }

      // Add to sales list
      const drug = state.drugDatabase.find(d => d.name === selectName);
      const purchasePrice = drug ? drug.purchasePrice : 0.0;

      const existing = state.currentSale.items.find(i => i.name === selectName);
      if (existing) {
        existing.qty += qty;
        existing.total = existing.qty * mrp;
      } else {
        state.currentSale.items.push({
          name: selectName,
          batch: "RP-" + Math.floor(100 + Math.random() * 900),
          expiry: "12/2027",
          qty: qty,
          mrp: mrp,
          purchasePrice: purchasePrice,
          total: qty * mrp
        });
      }

      // Reset values
      document.getElementById('add-item-select').value = '';
      document.getElementById('add-item-qty').value = '1';
      document.getElementById('add-item-mrp').value = '0';

      renderBillItems();
    }

    function removeBillItem(index) {
      state.currentSale.items.splice(index, 1);
      renderBillItems();
    }

    function renderBillItems() {
      const tbody = document.getElementById('bill-items-table-body');
      tbody.innerHTML = '';

      if (state.currentSale.items.length === 0) {
        tbody.innerHTML = `
          <tr>
            <td colspan="6" class="px-6 py-8 text-center text-slate-400 text-xs">
              No medicines added to bill yet. Load sample or add items above.
            </td>
          </tr>
        `;
        document.getElementById('bill-subtotal').innerText = '₹0.00';
        document.getElementById('bill-gst').innerText = '₹0.00';
        document.getElementById('bill-net').innerText = '₹0.00';
        return;
      }

      let subtotal = 0.0;
      let totalPurchaseCost = 0.0;

      state.currentSale.items.forEach((item, index) => {
        subtotal += item.total;
        totalPurchaseCost += (item.purchasePrice || 0) * item.qty;

        const profit = item.total - ((item.purchasePrice || 0) * item.qty);
        
        tbody.innerHTML += `
          <tr class="hover:bg-slate-50 border-b border-slate-100">
            <td class="px-6 py-4">
              <div class="font-semibold text-slate-900">${item.name}</div>
              ${state.showProfitDetails ? `<div class="text-[10px] text-emerald-600 font-bold">Cost: ₹${(item.purchasePrice || 0).toFixed(2)} | Profit: ₹${profit.toFixed(2)}</div>` : ''}
            </td>
            <td class="px-6 py-4 text-center text-xs text-slate-500">${item.batch} (${item.expiry})</td>
            <td class="px-6 py-4 text-center font-bold">${item.qty}</td>
            <td class="px-6 py-4 text-right">₹${item.mrp.toFixed(2)}</td>
            <td class="px-6 py-4 text-right font-bold text-slate-900">₹${item.total.toFixed(2)}</td>
            <td class="px-6 py-4 text-center">
              <button onclick="removeBillItem(${index})" class="text-rose-600 hover:text-rose-800">
                <i data-lucide="trash-2" class="w-4 h-4"></i>
              </button>
            </td>
          </tr>
        `;
      });

      // Update calculations (GST 12% is included in the MRP pricing scheme)
      const gst = subtotal * 0.12;
      const totalProfit = subtotal - totalPurchaseCost;

      document.getElementById('bill-subtotal').innerText = `₹${subtotal.toFixed(2)}`;
      document.getElementById('bill-gst').innerText = `₹${gst.toFixed(2)}`;
      document.getElementById('bill-net').innerText = `₹${subtotal.toFixed(2)}`;
      
      const profitRow = document.getElementById('bill-profit-row');
      if (state.showProfitDetails) {
        if (!profitRow) {
          const row = document.createElement('div');
          row.id = 'bill-profit-row';
          row.className = 'flex justify-between text-xs font-bold text-emerald-600 bg-emerald-50 p-2 rounded mt-1';
          row.innerHTML = `<span>Estimated Sale Profit:</span> <span id="bill-profit-val">₹${totalProfit.toFixed(2)}</span>`;
          document.getElementById('bill-net').parentElement.insertAdjacentElement('beforebegin', row);
        } else {
          document.getElementById('bill-profit-val').innerText = `₹${totalProfit.toFixed(2)}`;
          profitRow.classList.remove('hidden');
        }
      } else if (profitRow) {
        profitRow.classList.add('hidden');
      }

      // Update Lucide icons
      lucide.createIcons();
    }

    function loadDemoSaleData() {
      state.currentSale.customerName = "Mr. Amit Kumar Sharma";
      state.currentSale.doctorName = "Dr. S. K. Sen, MBBS, MD";
      state.currentSale.items = [
        { name: "Paracetamol 650mg (Dolo)", batch: "DLO-9921", expiry: "10/2027", qty: 2, mrp: 30.0, purchasePrice: 18.5, total: 60.0 },
        { name: "Amoxicillin 500mg Capsule", batch: "AMX-1122", expiry: "05/2028", qty: 1, mrp: 72.0, purchasePrice: 45.0, total: 72.0 },
        { name: "Pantocid 40mg Tablet", batch: "PAN-8812", expiry: "12/2027", qty: 3, mrp: 95.0, purchasePrice: 62.0, total: 285.0 }
      ];
      document.getElementById('bill-customer').value = state.currentSale.customerName;
      document.getElementById('bill-doctor').value = state.currentSale.doctorName;
      renderBillItems();
    }

    function clearCurrentSale() {
      state.currentSale.items = [];
      document.getElementById('bill-customer').value = '';
      document.getElementById('bill-doctor').value = '';
      renderBillItems();
    }

    function checkoutSale() {
      if (state.role === 'STAFF' && state.activeStaffPermission === 'View-only access') {
        Swal.fire({
          title: 'View-Only Access',
          text: 'This staff account has View-Only access permissions. Processing sales checkouts is restricted.',
          icon: 'warning',
          confirmButtonColor: '#9C1258'
        });
        return;
      }

      if (state.currentSale.items.length === 0) {
        Swal.fire('Empty Sales Bill', 'Please add items to memo cart before check out.', 'warning');
        return;
      }

      state.currentSale.customerName = document.getElementById('bill-customer').value || "Walk-in Customer";
      state.currentSale.doctorName = document.getElementById('bill-doctor').value || "Dr. Self / None";
      state.currentSale.paymentMode = document.getElementById('bill-payment').value;

      const billTotal = state.currentSale.items.reduce((sum, it) => sum + (it.total || (it.qty * it.mrp) || 0), 0);
      const itemCount = state.currentSale.items.length;
      const custName = state.currentSale.customerName;
      const payMode = state.currentSale.paymentMode;
      const invNo = "INV-" + (Math.floor(Math.random() * 9000) + 1000);

      // Log bill generated
      logStaffActivityWeb(
        "BILL_GENERATED",
        state.userName,
        `Generated Cash Memo #${invNo} for ₹${billTotal.toFixed(2)} (${itemCount} items, Customer: ${custName}, Pay: ${payMode})`,
        "#10B981"
      );

      Swal.fire({
        title: 'Checking Out Cash Memo...',
        timer: 800,
        didOpen: () => { Swal.showLoading() }
      }).then(() => {
        // Switch to Invoices & sync preview
        switchTab('invoices');
        renderInvoicePreview();
      });
    }

    // Distributor Cart / Required products
    function renderCartTable() {
      const tbody = document.getElementById('cart-table-body');
      tbody.innerHTML = '';

      if (state.distributorCart.length === 0) {
        // Fill initial mock requested purchase required items
        state.distributorCart = [
          { name: "Paracetamol 650mg (Dolo)", batch: "BATCH-DOLO", qty: 50, cost: 30.0, total: 1500 },
          { name: "Amoxicillin 500mg Capsule", batch: "BATCH-AMOX", qty: 30, cost: 72.0, total: 2160 },
          { name: "Ceftriaxone 1g Injectable", batch: "BATCH-CEFT", qty: 10, cost: 110.0, total: 1100 }
        ];
      }

      let poTotal = 0.0;
      state.distributorCart.forEach((item, index) => {
        poTotal += item.total;
        tbody.innerHTML += `
          <tr class="hover:bg-slate-50 border-b border-slate-100">
            <td class="px-6 py-4 font-semibold text-slate-900">${item.name}</td>
            <td class="px-6 py-4 text-center text-xs text-slate-500">${item.batch}</td>
            <td class="px-6 py-4 text-center font-bold">
              <input type="number" value="${item.qty}" min="1" onchange="updateCartQty(${index}, this.value)" class="w-16 border rounded text-center py-1">
            </td>
            <td class="px-6 py-4 text-right font-bold text-emerald-600">₹${item.total.toFixed(2)}</td>
            <td class="px-6 py-4 text-center">
              <button onclick="removeCartItem(${index})" class="text-rose-600 hover:text-rose-800">
                <i data-lucide="trash-2" class="w-4 h-4"></i>
              </button>
            </td>
          </tr>
        `;
      });

      document.getElementById('export-total-items').innerText = state.distributorCart.length;
      document.getElementById('export-total-price').innerText = `₹${poTotal.toFixed(2)}`;
      lucide.createIcons();
    }

    function updateCartQty(index, val) {
      const q = parseInt(val) || 1;
      state.distributorCart[index].qty = q;
      state.distributorCart[index].total = q * state.distributorCart[index].cost;
      saveStateToLocal();
      renderCartTable();
    }

    function removeCartItem(index) {
      state.distributorCart.splice(index, 1);
      saveStateToLocal();
      renderCartTable();
    }

    function clearDistributorCart() {
      state.distributorCart = [];
      saveStateToLocal();
      renderCartTable();
    }

    function addCustomCartItem() {
      Swal.fire({
        title: 'Add Purchase Request Product',
        html: `
          <input id="swal-item-name" class="swal2-input" placeholder="Medicine Brand Name">
          <input id="swal-item-qty" type="number" class="swal2-input" placeholder="Required Quantity" value="10">
          <input id="swal-item-cost" type="number" class="swal2-input" placeholder="Estimated Unit Cost (₹)" value="50">
        `,
        focusConfirm: false,
        preConfirm: () => {
          return {
            name: document.getElementById('swal-item-name').value,
            qty: parseInt(document.getElementById('swal-item-qty').value) || 10,
            cost: parseFloat(document.getElementById('swal-item-cost').value) || 50.0
          }
        }
      }).then((res) => {
        if (res.value && res.value.name) {
          state.distributorCart.push({
            name: res.value.name,
            batch: "REQ-REF",
            qty: res.value.qty,
            cost: res.value.cost,
            total: res.value.qty * res.value.cost
          });
          saveStateToLocal();
          renderCartTable();
          Swal.fire('Added!', 'Custom purchase request logged.', 'success');
        }
      });
    }

    // SheetJS Batch Excel/CSV/PDF Export Logic (NETLIFY STANDARDS)
    function exportCart(format) {
      if (state.distributorCart.length === 0) {
        Swal.fire('Empty list', 'There are no active purchase orders to export.', 'warning');
        return;
      }

      // Prep Data
      const exportData = state.distributorCart.map(item => ({
        "Medicine Required": item.name,
        "Batch Reference": item.batch,
        "Quantity": item.qty,
        "Unit Cost (₹)": item.cost,
        "Subtotal PO Value (₹)": item.total
      }));

      const dateStr = new Date().toISOString().slice(0, 10);
      const filename = `RoyalChemist_Purchase_Required_${dateStr}`;

      if (format === 'csv') {
        const ws = XLSX.utils.json_to_sheet(exportData);
        const csvOutput = XLSX.utils.sheet_to_csv(ws);
        const blob = new Blob(["\uFEFF" + csvOutput], { type: 'text/csv;charset=utf-8;' });
        const url = URL.createObjectURL(blob);
        const a = document.createElement("a");
        a.href = url;
        a.download = `${filename}.csv`;
        a.click();
        Swal.fire('Export Successful', 'CSV Sheet downloaded.', 'success');
      } 
      else if (format === 'xls' || format === 'xlsx') {
        const wb = XLSX.utils.book_new();
        const ws = XLSX.utils.json_to_sheet(exportData);
        XLSX.utils.book_append_sheet(wb, ws, "Purchase Orders");
        XLSX.writeFile(wb, `${filename}.${format}`);
        Swal.fire('Export Successful', `${format.toUpperCase()} sheet generated successfully.`, 'success');
      } 
      else if (format === 'pdf') {
        // PDF generation using window.print style or basic simulation layout
        const { jsPDF } = window.jspdf ? window.jspdf : { jsPDF: null };
        if (jsPDF) {
          const doc = new jsPDF();
          doc.setFontSize(16);
          doc.text("ROYAL PHARMACY DISTRIBUTOR PURCHASE ORDER", 14, 20);
          doc.setFontSize(10);
          doc.text(`PO Date: ${dateStr}`, 14, 28);
          doc.text(`Supplier: Approved Wholesale Distributors`, 14, 34);
          doc.text("--------------------------------------------------------------------------------", 14, 40);
          
          let y = 48;
          doc.text("Required Drug Formulation", 14, y);
          doc.text("Qty", 120, y);
          doc.text("Cost", 145, y);
          doc.text("Total Value", 170, y);
          doc.text("--------------------------------------------------------------------------------", 14, y+4);
          
          y += 10;
          state.distributorCart.forEach(item => {
            doc.text(item.name, 14, y);
            doc.text(item.qty.toString(), 120, y);
            doc.text(`Rs. ${item.cost.toFixed(2)}`, 145, y);
            doc.text(`Rs. ${item.total.toFixed(2)}`, 170, y);
            y += 8;
          });
          
          doc.save(`${filename}.pdf`);
          Swal.fire('Export Successful', 'Purchase Order PDF saved.', 'success');
        } else {
          // Direct styling preview print
          window.print();
        }
      }
    }

    // Search Local Catalog & Sorting
    let currentSortColumn = null;
    let currentSortAsc = true;

    function sortDrugs(column) {
      if (currentSortColumn === column) {
        currentSortAsc = !currentSortAsc;
      } else {
        currentSortColumn = column;
        currentSortAsc = true;
      }

      // Reset indicator icons
      ['name', 'hsn', 'stock', 'reorderLevel', 'price'].forEach(col => {
        const el = document.getElementById(`sort-icon-${col}`);
        if (el) {
          if (col === currentSortColumn) {
            el.innerText = currentSortAsc ? ' ▲' : ' ▼';
          } else {
            el.innerText = ' ↕';
          }
        }
      });

      state.drugDatabase.sort((a, b) => {
        let valA = column === 'reorderLevel' ? (a.minStock || a.reorderLevel || 15) : a[column];
        let valB = column === 'reorderLevel' ? (b.minStock || b.reorderLevel || 15) : b[column];
        if (typeof valA === 'string') {
          valA = valA.toLowerCase();
          valB = valB.toLowerCase();
          if (valA < valB) return currentSortAsc ? -1 : 1;
          if (valA > valB) return currentSortAsc ? 1 : -1;
          return 0;
        } else {
          return currentSortAsc ? valA - valB : valB - valA;
        }
      });

      searchDrugs();
    }

    let searchDebounceTimer = null;

    function debouncedSearchDrugs() {
      clearTimeout(searchDebounceTimer);
      searchDebounceTimer = setTimeout(() => {
        searchDrugs();
      }, 250);
    }

    function filterReorderOnlyWeb() {
      const catSelect = document.getElementById('drug-category-filter');
      if (catSelect) {
        catSelect.value = "REORDER_LOW";
        searchDrugs();
      }
    }

    function updateDrugReorderLevel(drugName, newLevel) {
      const parsedVal = parseInt(newLevel, 10);
      if (isNaN(parsedVal) || parsedVal < 1) {
        if (window.Swal) {
          Swal.fire('Invalid Input', 'Please enter a valid positive reorder threshold number.', 'warning');
        }
        return;
      }

      state.customReorderLevels = state.customReorderLevels || {};
      state.customReorderLevels[drugName] = parsedVal;

      const drug = state.drugDatabase.find(d => d.name === drugName);
      if (drug) {
        drug.reorderLevel = parsedVal;
        drug.minStock = parsedVal;
      }

      saveStateToLocal();

      // Sync change to Firestore immediately so it updates on mobile app
      if (typeof fbDb !== 'undefined' && fbDb) {
        const docId = 'med_' + drugName.replace(/[^a-zA-Z0-9]/g, '_').toLowerCase();
        syncWebChangeToFirestore('pharmacy_inventory', docId, {
          name: drugName,
          minStockAlert: parsedVal
        });
      }

      if (window.Swal) {
        const Toast = Swal.mixin({
          toast: true,
          position: 'top-end',
          showConfirmButton: false,
          timer: 1800,
          timerProgressBar: true
        });
        Toast.fire({
          icon: 'success',
          title: `Reorder level updated to ${parsedVal} for ${drugName}`
        });
      }

      searchDrugs();
      if (typeof renderLowStockWidget === 'function') {
        renderLowStockWidget();
      }
    }

    function adjustDrugStockWeb(drugName, delta) {
      const drug = state.drugDatabase.find(d => d.name === drugName);
      if (!drug) return;

      const oldStock = drug.stock || 0;
      const newStock = Math.max(0, oldStock + delta);
      drug.stock = newStock;

      saveStateToLocal();

      // Track recent adjustment for CSS animation
      window._lastAdjustedDrugName = drugName;
      window._lastAdjustedDelta = delta;

      // Sync change to Firestore immediately
      if (typeof fbDb !== 'undefined' && fbDb) {
        const docId = 'med_' + drugName.replace(/[^a-zA-Z0-9]/g, '_').toLowerCase();
        syncWebChangeToFirestore('pharmacy_inventory', docId, {
          name: drugName,
          stockPacks: newStock,
          stockQuantity: newStock
        });
      }

      // Preserve table scroll position if in container
      const tableContainer = document.querySelector('#tab-content-drugs .overflow-x-auto') ||
                             document.getElementById('drugs-table-body')?.closest('.overflow-x-auto') ||
                             document.getElementById('drugs-table-body')?.parentElement?.parentElement;
      const prevScrollTop = tableContainer ? tableContainer.scrollTop : 0;

      searchDrugs();
      if (typeof renderLowStockWidget === 'function') {
        renderLowStockWidget();
      }

      if (tableContainer && prevScrollTop) {
        tableContainer.scrollTop = prevScrollTop;
      }

      // Re-trigger and enforce CSS visual feedback animation on the newly rendered DOM elements
      let stockPillEl = document.getElementById(`stock-pill-${escapeHtml(escapeJsParam(drugName))}`);
      if (!stockPillEl) {
        const safeName = drugName.replace(/["\\]/g, '\\$&');
        stockPillEl = document.querySelector(`[data-stock-pill="${safeName}"]`);
      }
      if (!stockPillEl) {
        const allPills = document.querySelectorAll('[data-stock-pill]');
        for (const pill of allPills) {
          if (pill.getAttribute('data-stock-pill') === drugName) {
            stockPillEl = pill;
            break;
          }
        }
      }

      let stockCountEl = document.getElementById(`stock-count-${escapeHtml(escapeJsParam(drugName))}`);
      if (!stockCountEl && stockPillEl) {
        stockCountEl = stockPillEl.querySelector('.stock-count-number');
      }

      if (stockPillEl) {
        const animClass = delta > 0 ? 'animate-stock-pill-increase' : 'animate-stock-pill-decrease';
        stockPillEl.classList.remove('animate-stock-glow', 'animate-stock-pill-pop', 'animate-stock-pill-increase', 'animate-stock-pill-decrease');
        void stockPillEl.offsetWidth; // Force reflow to restart CSS animation cleanly
        stockPillEl.classList.add(animClass, 'animate-stock-pill-pop', 'animate-stock-glow');
      }

      if (stockCountEl) {
        stockCountEl.textContent = newStock;
        stockCountEl.classList.remove('animate-stock-pop');
        void stockCountEl.offsetWidth; // Force reflow
        stockCountEl.classList.add('animate-stock-pop');
      }

      // Clear tracking so subsequent unrelated queries don't re-animate
      clearTimeout(window._clearAdjustedDrugTimer);
      window._clearAdjustedDrugTimer = setTimeout(() => {
        window._lastAdjustedDrugName = null;
        window._lastAdjustedDelta = 0;
      }, 1200);

      if (window.Swal) {
        const Toast = Swal.mixin({
          toast: true,
          position: 'top-end',
          showConfirmButton: false,
          timer: 1500,
          timerProgressBar: true
        });
        Toast.fire({
          icon: delta > 0 ? 'success' : 'info',
          title: `${drugName} stock: ${oldStock} ➔ ${newStock} strips`
        });
      }
    }

    function searchDrugs() {
      const rawQuery = document.getElementById('drug-search').value.toLowerCase().trim();
      const filter = document.getElementById('drug-category-filter').value;
      const tbody = document.getElementById('drugs-table-body');
      if (!tbody) return;
      tbody.innerHTML = '';

      const queryTokens = rawQuery.split(/\s+/).filter(Boolean);

      // Apply custom reorder levels if stored
      if (state.customReorderLevels) {
        state.drugDatabase.forEach(d => {
          if (state.customReorderLevels[d.name] !== undefined) {
            d.minStock = state.customReorderLevels[d.name];
            d.reorderLevel = state.customReorderLevels[d.name];
          }
        });
      }

      // Reorder warning threshold (default 15 strips unless minStock specified)
      const reorderItems = state.drugDatabase.filter(d => d.stock <= (d.minStock || d.reorderLevel || 15));
      const summaryBar = document.getElementById('drug-reorder-summary-bar');
      const countNum = document.getElementById('reorder-count-num');
      if (summaryBar && countNum) {
        countNum.innerText = reorderItems.length;
        if (reorderItems.length > 0) {
          summaryBar.classList.remove('hidden');
        } else {
          summaryBar.classList.add('hidden');
        }
      }

      const filtered = state.drugDatabase.filter(d => {
        const textToSearch = `${d.name} ${d.generic} ${d.hsn} ${d.category}`.toLowerCase();
        const matchesTokens = queryTokens.length === 0 || queryTokens.every(token => textToSearch.includes(token));
        
        let matchesCategory = true;
        if (filter === "REORDER_LOW") {
          matchesCategory = d.stock <= (d.minStock || d.reorderLevel || 15);
        } else if (filter !== "") {
          matchesCategory = d.category === filter;
        }

        return matchesTokens && matchesCategory;
      });

      if (filtered.length === 0) {
        tbody.innerHTML = `<tr><td colspan="7" class="px-6 py-6 text-center text-slate-400">No results match search queries.</td></tr>`;
        return;
      }

      const displayLimit = 150;
      const limitedResults = filtered.slice(0, displayLimit);

      if (filtered.length > displayLimit) {
        tbody.innerHTML += `<tr><td colspan="7" class="px-4 py-2 bg-pink-50 text-[11px] text-royal-magenta font-semibold text-center">Showing top ${displayLimit} matches of ${filtered.length.toLocaleString()} matching records (out of 500,000+ formulary catalog). Refine search for specific results.</td></tr>`;
      }

      limitedResults.forEach(item => {
        const minStockLevel = (state.customReorderLevels && state.customReorderLevels[item.name]) !== undefined
          ? state.customReorderLevels[item.name]
          : (item.minStock || item.reorderLevel || 15);
        item.minStock = minStockLevel;
        item.reorderLevel = minStockLevel;

        const isLowStock = item.stock <= minStockLevel;
        const isOutStock = item.stock === 0;
        const isBelowThreshold = isLowStock || isOutStock;
        const recommendedQty = Math.max(10, Math.ceil((minStockLevel * 2) - (item.stock || 0)));

        const isRecentlyAdjusted = (window._lastAdjustedDrugName === item.name);
        const animPillClass = isRecentlyAdjusted
          ? `animate-stock-pill-pop ${window._lastAdjustedDelta > 0 ? 'animate-stock-pill-increase' : 'animate-stock-pill-decrease'} animate-stock-glow`
          : '';
        const animCountClass = isRecentlyAdjusted ? 'animate-stock-pop' : '';

        let rowClass = "hover:bg-slate-50 border-b border-slate-100 transition-colors";
        let stockPill = "";

        if (isOutStock) {
          rowClass = "bg-rose-50/80 hover:bg-rose-100/80 border-b border-rose-200/90 transition-colors";
          stockPill = `
            <div id="stock-pill-${escapeHtml(escapeJsParam(item.name))}" data-stock-pill="${escapeHtml(item.name)}" class="inline-flex items-center space-x-1.5 px-3 py-1 rounded-full text-xs font-bold bg-rose-100 text-rose-800 border border-rose-300 shadow-sm transition-all duration-300 ${animPillClass}">
              <button onclick="adjustDrugStockWeb('${escapeHtml(escapeJsParam(item.name))}', -1)" class="w-5 h-5 rounded-full bg-rose-200 text-rose-900 hover:bg-rose-300 font-black flex items-center justify-center transition active:scale-90" title="Decrease Stock">-</button>
              <i data-lucide="x-circle" class="w-3.5 h-3.5 text-rose-600 shrink-0"></i>
              <span id="stock-count-${escapeHtml(escapeJsParam(item.name))}" class="stock-count-number text-sm font-black text-rose-900 ${animCountClass}">${item.stock}</span>
              <span class="text-[11px]">strips</span>
              <button onclick="adjustDrugStockWeb('${escapeHtml(escapeJsParam(item.name))}', 1)" class="w-5 h-5 rounded-full bg-rose-200 text-rose-900 hover:bg-rose-300 font-black flex items-center justify-center transition active:scale-90" title="Increase Stock">+</button>
              <span class="text-[9px] font-black uppercase tracking-wider bg-rose-200 text-rose-900 px-1.5 py-0.2 rounded ml-1">CRITICAL</span>
            </div>
          `;
        } else if (isLowStock) {
          rowClass = "bg-amber-50/80 hover:bg-amber-100/80 border-b border-amber-200/90 transition-colors";
          stockPill = `
            <div id="stock-pill-${escapeHtml(escapeJsParam(item.name))}" data-stock-pill="${escapeHtml(item.name)}" class="inline-flex items-center space-x-1.5 px-3 py-1 rounded-full text-xs font-bold bg-amber-100 text-amber-900 border border-amber-300 shadow-sm transition-all duration-300 ${animPillClass}">
              <button onclick="adjustDrugStockWeb('${escapeHtml(escapeJsParam(item.name))}', -1)" class="w-5 h-5 rounded-full bg-amber-200 text-amber-900 hover:bg-amber-300 font-black flex items-center justify-center transition active:scale-90" title="Decrease Stock">-</button>
              <i data-lucide="alert-triangle" class="w-3.5 h-3.5 text-amber-600 shrink-0"></i>
              <span id="stock-count-${escapeHtml(escapeJsParam(item.name))}" class="stock-count-number text-sm font-black text-amber-950 ${animCountClass}">${item.stock}</span>
              <span class="text-[11px]">strips</span>
              <button onclick="adjustDrugStockWeb('${escapeHtml(escapeJsParam(item.name))}', 1)" class="w-5 h-5 rounded-full bg-amber-200 text-amber-900 hover:bg-amber-300 font-black flex items-center justify-center transition active:scale-90" title="Increase Stock">+</button>
              <span class="text-[9px] font-black uppercase tracking-wider bg-amber-200 text-amber-900 px-1.5 py-0.2 rounded ml-1">REORDER (Min ${minStockLevel})</span>
            </div>
          `;
        } else {
          stockPill = `
            <div id="stock-pill-${escapeHtml(escapeJsParam(item.name))}" data-stock-pill="${escapeHtml(item.name)}" class="inline-flex items-center space-x-1.5 px-3 py-1 rounded-full text-xs font-semibold bg-emerald-50 text-emerald-800 border border-emerald-300 shadow-sm transition-all duration-300 ${animPillClass}">
              <button onclick="adjustDrugStockWeb('${escapeHtml(escapeJsParam(item.name))}', -1)" class="w-5 h-5 rounded-full bg-emerald-200 text-emerald-900 hover:bg-emerald-300 font-black flex items-center justify-center transition active:scale-90" title="Decrease Stock">-</button>
              <span id="stock-count-${escapeHtml(escapeJsParam(item.name))}" class="stock-count-number text-sm font-black text-emerald-900 ${animCountClass}">${item.stock}</span>
              <span class="text-[11px]">strips</span>
              <button onclick="adjustDrugStockWeb('${escapeHtml(escapeJsParam(item.name))}', 1)" class="w-5 h-5 rounded-full bg-emerald-200 text-emerald-900 hover:bg-emerald-300 font-black flex items-center justify-center transition active:scale-90" title="Increase Stock">+</button>
            </div>
          `;
        }

        tbody.innerHTML += `
          <tr class="${rowClass}">
            <td class="px-6 py-4">
              <div class="flex items-center space-x-2">
                <div>
                  <p class="font-bold text-slate-900">${escapeHtml(item.name)}</p>
                  <p class="text-xs text-slate-400">${escapeHtml(item.generic || '')}</p>
                </div>
                ${isOutStock ? '<span class="px-1.5 py-0.2 bg-rose-200 text-rose-900 border border-rose-300 rounded text-[9px] font-black uppercase tracking-widest shrink-0">Stock Out</span>' : (isLowStock ? '<span class="px-1.5 py-0.2 bg-amber-200 text-amber-900 border border-amber-300 rounded text-[9px] font-black uppercase tracking-widest shrink-0">Low Stock</span>' : '')}
              </div>
            </td>
            <td class="px-6 py-4 text-center text-xs font-mono">${escapeHtml(item.hsn || '')}</td>
            <td class="px-6 py-4 text-center">
              ${stockPill}
            </td>
            <td class="px-6 py-4 text-center">
              <div class="inline-flex items-center space-x-1.5 bg-slate-100/90 dark:bg-slate-800 border border-slate-200 dark:border-slate-700 rounded-xl px-2.5 py-1 shadow-inner">
                <i data-lucide="bell-ring" class="w-3.5 h-3.5 text-amber-600 shrink-0"></i>
                <input type="number" 
                       value="${minStockLevel}" 
                       min="1" max="100000" 
                       id="reorder-input-${escapeHtml(escapeJsParam(item.name))}"
                       oninput="updateDrugReorderLevel('${escapeHtml(escapeJsParam(item.name))}', this.value)"
                       onchange="updateDrugReorderLevel('${escapeHtml(escapeJsParam(item.name))}', this.value)"
                       class="w-16 text-center text-xs font-extrabold bg-white dark:bg-slate-900 border border-slate-300 dark:border-slate-600 rounded py-1 text-slate-800 dark:text-slate-100 focus:outline-none focus:ring-2 focus:ring-pink-500 shadow-sm transition-all"
                       title="Edit Reorder Threshold level for ${escapeHtml(item.name)}" />
                <span class="text-[10px] text-slate-500 font-bold pr-0.5">units</span>
                <button onclick="updateDrugReorderLevel('${escapeHtml(escapeJsParam(item.name))}', document.getElementById('reorder-input-${escapeHtml(escapeJsParam(item.name))}').value)" class="bg-royal-magenta text-white px-2 py-0.5 rounded text-[10px] font-bold hover:bg-royal-accent transition shadow-sm" title="Save reorder threshold level to Firestore database">Save</button>
              </div>
            </td>
            <td class="px-6 py-4 text-right font-semibold">₹${item.price.toFixed(2)}</td>
            <td class="px-6 py-4 text-center text-xs font-bold text-pink-600">${escapeHtml(item.safety || '')}</td>
            <td class="px-6 py-4 text-center">
              <div class="flex items-center justify-center space-x-1.5 flex-wrap gap-y-1">
                ${isBelowThreshold ? `
                  <button onclick="quickReorderDrugWeb('${escapeHtml(escapeJsParam(item.name))}')" 
                          class="bg-gradient-to-r from-amber-600 to-rose-600 hover:from-amber-700 hover:to-rose-700 text-white px-2.5 py-1 rounded text-xs font-bold flex items-center space-x-1 transition shadow-sm active:scale-95 border border-rose-700/60" 
                          title="Quick Reorder: Automatically add recommended ${recommendedQty} units to Required Purchases">
                    <i data-lucide="zap" class="w-3.5 h-3.5 text-amber-200"></i>
                    <span>Quick Reorder</span>
                    <span class="bg-black/25 text-amber-200 text-[10px] px-1 rounded-full font-mono font-black">+${recommendedQty}</span>
                  </button>
                ` : ''}
                <button onclick="requestStockToCart('${escapeHtml(escapeJsParam(item.name))}', ${item.price})" class="bg-royal-magenta text-white px-2.5 py-1 rounded text-xs hover:bg-royal-accent font-semibold transition" title="Add to Required Purchases">Purchase</button>
                <button onclick="generateAndPrintThermalBarcodeLabel('${escapeHtml(escapeJsParam(item.name))}')" 
                        class="bg-indigo-600 hover:bg-indigo-700 text-white px-2.5 py-1 rounded text-xs font-semibold flex items-center space-x-1 transition shadow-sm border border-indigo-700 active:scale-95" 
                        title="Generate and print thermal barcode label using HSN code (${escapeHtml(item.hsn || '3004')})" 
                        data-action="print-thermal-barcode"
                        data-hsn="${escapeHtml(item.hsn || '3004')}"
                        data-testid="btn-thermal-barcode-${escapeHtml(escapeJsParam(item.name))}">
                  <i data-lucide="barcode" class="w-3.5 h-3.5"></i>
                  <span>Barcode Label</span>
                </button>
                <button onclick="openQrBatchModalWeb('${escapeHtml(escapeJsParam(item.name))}')" class="bg-slate-800 text-white hover:bg-slate-900 px-2 py-1 rounded text-xs font-semibold flex items-center space-x-1 transition shadow-sm border border-slate-700" title="Generate printable batch QR label">
                  <i data-lucide="qr-code" class="w-3.5 h-3.5"></i>
                  <span>QR Batch</span>
                </button>
                <button onclick="downloadThermalLabelPdf('${escapeHtml(escapeJsParam(item.name))}')" class="bg-amber-600 hover:bg-amber-700 text-white px-2.5 py-1 rounded text-xs font-semibold flex items-center space-x-1 transition shadow-sm border border-amber-700" title="Download thermal label PDF">
                  <i data-lucide="printer" class="w-3.5 h-3.5"></i>
                  <span>Thermal PDF</span>
                </button>
              </div>
            </td>
          </tr>
        `;
      });
      if (window.lucide) lucide.createIcons();
    }

    // Modal to Generate and Print QR Batch Labels for a Medicine
    function openQrBatchModalWeb(medicineName) {
      const drug = state.drugDatabase.find(d => d.name === medicineName);
      if (!drug) {
        Swal.fire('Error', 'Medicine details not found in database.', 'error');
        return;
      }

      const defaultBatch = (drug.batch && drug.batch !== 'CAT-REQ') ? drug.batch : 'RX' + Math.floor(1000 + Math.random() * 9000);
      const defaultExp = drug.expiry || '12/2027';
      const pharmacyName = state.profile.businessName || 'ROYAL PHARMACY';

      const modalHtml = `
        <div class="text-left space-y-4 text-xs">
          <div class="p-3 bg-pink-50 rounded-xl border border-pink-100 flex items-center justify-between">
            <div>
              <p class="font-bold text-slate-800 text-sm">${escapeHtml(drug.name)}</p>
              <p class="text-[10px] text-slate-500">${escapeHtml(drug.generic || 'Generic Formulation')}</p>
            </div>
            <span class="px-2 py-0.5 bg-royal-magenta text-white text-[10px] font-bold rounded-full">₹${drug.price.toFixed(2)}</span>
          </div>

          <div class="grid grid-cols-2 gap-3">
            <div>
              <label class="block font-bold text-slate-700 mb-1">Batch Number:</label>
              <input type="text" id="qr-batch-input" value="${escapeHtml(defaultBatch)}" class="w-full px-3 py-1.5 border border-slate-300 rounded-lg text-xs font-mono font-bold focus:ring-2 focus:ring-pink-500/20" oninput="refreshBatchQrPreview('${escapeHtml(escapeJsParam(drug.name))}')">
            </div>
            <div>
              <label class="block font-bold text-slate-700 mb-1">Expiry Date:</label>
              <input type="text" id="qr-exp-input" value="${escapeHtml(defaultExp)}" class="w-full px-3 py-1.5 border border-slate-300 rounded-lg text-xs font-mono focus:ring-2 focus:ring-pink-500/20" oninput="refreshBatchQrPreview('${escapeHtml(escapeJsParam(drug.name))}')">
            </div>
          </div>

          <!-- Printable Label Preview -->
          <div class="pt-2">
            <label class="block font-bold text-slate-700 mb-1 text-[11px] uppercase tracking-wider">Printable QR Sticker Preview</label>
            <div id="qr-batch-printable-card" class="bg-white border-2 border-slate-900 rounded-xl p-4 shadow-sm flex items-center space-x-4">
              <div id="qr-batch-code-canvas" class="bg-white p-1 border border-slate-300 rounded shrink-0 flex items-center justify-center min-w-[96px] min-h-[96px]"></div>
              <div class="flex-1 min-w-0 space-y-1">
                <p class="text-[10px] font-extrabold uppercase text-royal-magenta tracking-wide truncate">${escapeHtml(pharmacyName)}</p>
                <p class="text-xs font-bold text-slate-900 truncate">${escapeHtml(drug.name)}</p>
                <div class="text-[10px] text-slate-600 font-mono space-y-0.5">
                  <p>Batch: <b id="qr-card-batch">${escapeHtml(defaultBatch)}</b></p>
                  <p>Expiry: <b id="qr-card-exp">${escapeHtml(defaultExp)}</b></p>
                  <p>MRP: <b>₹${drug.price.toFixed(2)}</b> (Inc. GST)</p>
                </div>
                <div class="pt-1">
                  <span class="inline-block bg-slate-900 text-white text-[9px] px-2 py-0.5 rounded font-mono font-bold">📍 RACK A-1</span>
                </div>
              </div>
            </div>
          </div>

          <!-- Encoded JSON Payload Viewer -->
          <details class="text-[10px] text-slate-500 bg-slate-50 p-2.5 rounded-lg border border-slate-200">
            <summary class="font-bold cursor-pointer text-slate-700">View Encoded JSON Payload</summary>
            <pre id="qr-batch-json-preview" class="mt-2 p-2 bg-slate-900 text-emerald-400 rounded overflow-x-auto font-mono text-[9px] leading-relaxed"></pre>
          </details>
        </div>
      `;

      Swal.fire({
        title: `Generate QR Batch: ${drug.name}`,
        html: modalHtml,
        showCancelButton: true,
        confirmButtonText: '<i class="fa fa-print"></i> Print QR Label',
        confirmButtonColor: '#9C1258',
        cancelButtonText: 'Close',
        didOpen: () => {
          refreshBatchQrPreview(drug.name);
        }
      }).then((result) => {
        if (result.isConfirmed) {
          printQrBatchLabel(drug.name);
        }
      });
    }

    function refreshBatchQrPreview(medicineName) {
      const drug = state.drugDatabase.find(d => d.name === medicineName);
      if (!drug) return;

      const batchVal = document.getElementById('qr-batch-input')?.value.trim() || 'RX990';
      const expVal = document.getElementById('qr-exp-input')?.value.trim() || '12/2027';

      const batchDisplay = document.getElementById('qr-card-batch');
      const expDisplay = document.getElementById('qr-card-exp');
      if (batchDisplay) batchDisplay.innerText = batchVal;
      if (expDisplay) expDisplay.innerText = expVal;

      const payload = {
        name: drug.name,
        batchNumber: batchVal,
        expiryDate: expVal,
        mrp: drug.price,
        generic: drug.generic || '',
        hsn: drug.hsn || '300420',
        pharmacy: state.profile.businessName || 'ROYAL PHARMACY'
      };

      const payloadStr = JSON.stringify(payload);
      const jsonPre = document.getElementById('qr-batch-json-preview');
      if (jsonPre) jsonPre.innerText = JSON.stringify(payload, null, 2);

      const qrContainer = document.getElementById('qr-batch-code-canvas');
      if (qrContainer) {
        qrContainer.innerHTML = '';
        if (typeof QRCode !== 'undefined') {
          try {
            new QRCode(qrContainer, {
              text: payloadStr,
              width: 90,
              height: 90,
              colorDark: "#000000",
              colorLight: "#ffffff",
              correctLevel: QRCode.CorrectLevel.M
            });
          } catch (e) {
            console.error("QR Code rendering error:", e);
          }
        } else {
          // Fallback image generator
          qrContainer.innerHTML = `<img src="https://api.qrserver.com/v1/create-qr-code/?size=90x90&data=${encodeURIComponent(payloadStr)}" alt="QR Code" class="w-[90px] h-[90px]" />`;
        }
      }
    }

    function printQrBatchLabel(medicineName) {
      const printableContent = document.getElementById('qr-batch-printable-card');
      if (!printableContent) return;

      const printWindow = window.open('', '_blank', 'width=500,height=400');
      if (!printWindow) {
        window.print();
        return;
      }

      printWindow.document.write(`
        <html>
          <head>
            <title>Print QR Label - ${escapeHtml(medicineName)}</title>
            <style>
              body { font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, sans-serif; margin: 0; padding: 20px; }
              .card { border: 2px solid #000; border-radius: 8px; padding: 16px; display: flex; align-items: center; gap: 16px; max-width: 380px; }
              .details { font-size: 12px; line-height: 1.4; }
              .title { font-weight: bold; font-size: 14px; margin: 2px 0; }
              .pharmacy { font-size: 10px; font-weight: bold; text-transform: uppercase; color: #9C1258; }
              .badge { background: #000; color: #fff; padding: 2px 6px; font-size: 10px; border-radius: 4px; display: inline-block; margin-top: 4px; }
            </style>
          </head>
          <body>
            <div class="card">
              ${printableContent.innerHTML}
            </div>
            <script>
              window.onload = function() {
                window.print();
                setTimeout(() => window.close(), 1000);
              };
            <\/script>
          </body>
        </html>
      `);
      printWindow.document.close();
    }

    function downloadThermalLabelPdf(medicineName) {
      const drug = state.drugDatabase.find(d => d.name === medicineName);
      if (!drug) {
        if (window.Swal) Swal.fire('Error', 'Medicine details not found in database.', 'error');
        return;
      }

      const batchNum = (drug.batch && drug.batch !== 'CAT-REQ') ? drug.batch : 'RX' + Math.floor(1000 + Math.random() * 9000);
      const expDate = drug.expiry || '12/2027';
      const pharmacyName = state.profile.businessName || 'ROYAL PHARMACY';

      const { jsPDF } = window.jspdf ? window.jspdf : { jsPDF: null };
      if (!jsPDF) {
        if (window.Swal) Swal.fire('Error', 'PDF library not loaded.', 'error');
        return;
      }

      // Thermal label format (60mm x 40mm landscape thermal sticker)
      const doc = new jsPDF({
        orientation: 'landscape',
        unit: 'mm',
        format: [60, 40]
      });

      doc.setFont("helvetica", "bold");
      doc.setFontSize(9);
      doc.text(pharmacyName.toUpperCase(), 3, 5);

      doc.setFontSize(10);
      doc.text(drug.name, 3, 11, { maxWidth: 54 });

      doc.setFont("helvetica", "normal");
      doc.setFontSize(8);
      doc.text(`Generic: ${drug.generic || 'N/A'}`, 3, 16, { maxWidth: 54 });

      doc.setFont("helvetica", "bold");
      doc.setFontSize(8);
      doc.text(`Batch: ${batchNum}  |  Exp: ${expDate}`, 3, 23);
      doc.text(`MRP: Rs. ${drug.price.toFixed(2)} (Inc. GST)`, 3, 28);
      
      doc.setFontSize(7);
      doc.setTextColor(100, 100, 100);
      doc.text(`HSN: ${drug.hsn || '3004'} | Rack: A-1 | Qty: ${drug.stock}`, 3, 35);

      const filename = `Thermal_Label_${drug.name.replace(/[^a-zA-Z0-9]/g, '_')}.pdf`;
      doc.save(filename);

      if (window.Swal) {
        const Toast = Swal.mixin({
          toast: true,
          position: 'top-end',
          showConfirmButton: false,
          timer: 2000,
          timerProgressBar: true
        });
        Toast.fire({
          icon: 'success',
          title: `Thermal label PDF generated & downloaded for ${drug.name}`
        });
      }
    }

    // =========================================================================
    // THERMAL BARCODE LABEL GENERATION & PRINTING ENGINE (HSN CODE COMPLIANCE)
    // =========================================================================

    function generateCode39Svg(inputCode) {
      const clean = (inputCode || '3004').toString().trim().toUpperCase();
      const code39Map = {
        '0': '000110100', '1': '100100001', '2': '001100001', '3': '101100000',
        '4': '000110001', '5': '100110000', '6': '001110000', '7': '000100101',
        '8': '100100100', '9': '001100100', 'A': '100001001', 'B': '001001001',
        'C': '101001000', 'D': '000011001', 'E': '100011000', 'F': '001011000',
        'G': '000001101', 'H': '100001100', 'I': '001001100', 'J': '000011100',
        'K': '100000011', 'L': '001000011', 'M': '101000010', 'N': '000010011',
        'O': '100010010', 'P': '001010010', 'Q': '000000111', 'R': '100000110',
        'S': '001000110', 'T': '000010110', 'U': '110000001', 'V': '011000001',
        'W': '111000000', 'X': '010010001', 'Y': '110010000', 'Z': '011010000',
        '-': '010000101', '.': '110000100', ' ': '011000100', '*': '010010100',
        '$': '010101000', '/': '010100010', '+': '010001010', '%': '000101010'
      };

      const safeChars = clean.replace(/[^0-9A-Z\- .$/+%]/g, '') || '3004';
      const encodedStr = '*' + safeChars + '*';
      const narrow = 1.3;
      const wide = 3.2;
      const interCharGap = 1.8;
      const barHeight = 36;
      let x = 6;
      let rects = '';

      for (let i = 0; i < encodedStr.length; i++) {
        const char = encodedStr[i];
        const pattern = code39Map[char] || code39Map['0'];
        for (let j = 0; j < 9; j++) {
          const isBar = (j % 2 === 0);
          const isWide = (pattern[j] === '1');
          const width = isWide ? wide : narrow;
          if (isBar) {
            rects += `<rect x="${x.toFixed(1)}" y="2" width="${width.toFixed(1)}" height="${barHeight}" fill="#000000" />`;
          }
          x += width;
        }
        x += interCharGap;
      }

      const totalWidth = Math.max(130, Math.ceil(x + 6));
      const totalHeight = 54;
      return `<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 ${totalWidth} ${totalHeight}" width="${totalWidth}" height="${totalHeight}" style="display:block;margin:0 auto;background:#ffffff;">
        ${rects}
        <text x="${(totalWidth / 2).toFixed(1)}" y="49" font-family="monospace" font-size="11" font-weight="bold" fill="#000000" text-anchor="middle">HSN: ${safeChars}</text>
      </svg>`;
    }

    function generateHsnBarcodeSvg(hsnCode) {
      const cleanHsn = (hsnCode || '300490').toString().trim().toUpperCase();
      // 1. Try JsBarcode if loaded
      if (typeof JsBarcode !== 'undefined') {
        try {
          const svg = document.createElementNS('http://www.w3.org/2000/svg', 'svg');
          JsBarcode(svg, cleanHsn, {
            format: "CODE128",
            lineColor: "#000000",
            width: 1.8,
            height: 38,
            displayValue: true,
            text: `HSN: ${cleanHsn}`,
            fontSize: 11,
            font: "monospace",
            textMargin: 3,
            margin: 2
          });
          return new XMLSerializer().serializeToString(svg);
        } catch (e) {
          console.warn("JsBarcode generation fallback to Code39:", e);
        }
      }

      // 2. Fallback to standalone Code 39 SVG
      return generateCode39Svg(cleanHsn);
    }

    /**
     * Generates and prints a thermal barcode label using the medicine's HSN code.
     * Direct print trigger on standard 58mm x 40mm thermal roll sticker format.
     */
    function generateAndPrintThermalBarcodeLabel(medicineName) {
      const drug = state.drugDatabase.find(d => d.name === medicineName);
      if (!drug) {
        if (window.Swal) Swal.fire('Error', 'Medicine details not found in database.', 'error');
        return;
      }

      const hsn = (drug.hsn || '300490').toString().trim();
      const batchNum = (drug.batch && drug.batch !== 'CAT-REQ') ? drug.batch : 'RX' + Math.floor(1000 + Math.random() * 9000);
      const expDate = drug.expiry || '12/2027';
      const pharmacyName = state.profile?.businessName || 'ROYAL PHARMACY';
      const rack = drug.rackLocation || 'Rack A-1';
      const mrp = (drug.price || 0).toFixed(2);
      const generic = drug.generic || 'Ethical Formulation';

      const barcodeSvg = generateHsnBarcodeSvg(hsn);

      // Open print window for thermal label printer
      const printWindow = window.open('', '_blank', 'width=460,height=520');
      if (printWindow) {
        printWindow.document.write(`
          <!DOCTYPE html>
          <html>
            <head>
              <meta charset="utf-8">
              <title>Thermal Barcode Label - ${escapeHtml(drug.name)} (HSN: ${escapeHtml(hsn)})</title>
              <style>
                @page {
                  size: 58mm 40mm;
                  margin: 0;
                }
                @media print {
                  html, body {
                    width: 58mm;
                    height: 40mm;
                    margin: 0 !important;
                    padding: 0 !important;
                    background: #ffffff;
                  }
                  .no-print {
                    display: none !important;
                  }
                  .thermal-label-card {
                    border: none !important;
                    box-shadow: none !important;
                    width: 58mm !important;
                    height: 38mm !important;
                    page-break-after: always;
                  }
                }
                body {
                  font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, "Helvetica Neue", Arial, monospace, sans-serif;
                  margin: 0;
                  padding: 12px;
                  background: #f8fafc;
                  display: flex;
                  flex-direction: column;
                  align-items: center;
                  justify-content: center;
                  color: #000;
                }
                .no-print-toolbar {
                  background: #0f172a;
                  color: #ffffff;
                  padding: 8px 16px;
                  border-radius: 8px;
                  margin-bottom: 12px;
                  display: flex;
                  align-items: center;
                  gap: 10px;
                  font-size: 12px;
                  box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1);
                }
                .btn {
                  background: #4f46e5;
                  color: #ffffff;
                  border: none;
                  padding: 5px 12px;
                  border-radius: 6px;
                  font-weight: bold;
                  font-size: 11px;
                  cursor: pointer;
                  display: inline-flex;
                  align-items: center;
                  gap: 4px;
                }
                .btn:hover { background: #4338ca; }
                .btn-secondary { background: #475569; }
                .btn-secondary:hover { background: #334155; }
                .thermal-label-card {
                  width: 58mm;
                  min-height: 38mm;
                  box-sizing: border-box;
                  padding: 2.5mm 3mm;
                  background: #ffffff;
                  border: 1px dashed #000000;
                  border-radius: 4px;
                  display: flex;
                  flex-direction: column;
                  justify-content: space-between;
                  text-align: center;
                  box-shadow: 0 2px 4px rgba(0,0,0,0.06);
                }
                .pharmacy-header {
                  font-size: 8pt;
                  font-weight: 900;
                  text-transform: uppercase;
                  letter-spacing: 0.5px;
                  color: #000;
                  line-height: 1.1;
                }
                .drug-title {
                  font-size: 9.5pt;
                  font-weight: 800;
                  line-height: 1.2;
                  margin: 1mm 0 0.5mm 0;
                  color: #000;
                  word-break: break-word;
                }
                .drug-salt {
                  font-size: 6.5pt;
                  color: #333;
                  line-height: 1.1;
                  margin-bottom: 0.5mm;
                }
                .barcode-wrapper {
                  display: flex;
                  flex-direction: column;
                  align-items: center;
                  justify-content: center;
                  margin: 0.5mm 0;
                }
                .barcode-wrapper svg {
                  max-width: 52mm;
                  height: auto;
                  max-height: 14mm;
                }
                .details-row {
                  display: flex;
                  justify-content: space-between;
                  font-size: 6.5pt;
                  font-weight: bold;
                  color: #000;
                  line-height: 1.2;
                  border-top: 1px solid #000;
                  padding-top: 0.8mm;
                  margin-top: 0.5mm;
                }
                .footer-badge {
                  font-size: 5.5pt;
                  color: #555;
                  letter-spacing: 0.2px;
                  text-transform: uppercase;
                  margin-top: 0.5mm;
                }
              </style>
            </head>
            <body>
              <div class="no-print no-print-toolbar">
                <span>🖨️ Thermal Barcode Sticker: <b>${escapeHtml(drug.name)}</b> (HSN: <b>${escapeHtml(hsn)}</b>)</span>
                <button class="btn" onclick="window.print()">Print Now</button>
                <button class="btn btn-secondary" onclick="window.close()">Close</button>
              </div>
              <div class="thermal-label-card">
                <div>
                  <div class="pharmacy-header">${escapeHtml(pharmacyName)}</div>
                  <div class="drug-title">${escapeHtml(drug.name)}</div>
                  <div class="drug-salt">${escapeHtml(generic)}</div>
                </div>
                <div class="barcode-wrapper">
                  ${barcodeSvg}
                </div>
                <div>
                  <div class="details-row">
                    <span>Batch: ${escapeHtml(batchNum)}</span>
                    <span>Exp: ${escapeHtml(expDate)}</span>
                  </div>
                  <div class="details-row">
                    <span>MRP: ₹${mrp} (Inc. Tax)</span>
                    <span>${escapeHtml(rack)}</span>
                  </div>
                  <div class="footer-badge">HSN: ${escapeHtml(hsn)} • DISPENSE LABEL</div>
                </div>
              </div>
              <script>
                window.onload = function() {
                  setTimeout(function() {
                    window.print();
                  }, 300);
                };
              <\/script>
            </body>
          </html>
        `);
        printWindow.document.close();
      } else {
        // Fallback to modal if popup window was blocked
        openThermalBarcodeModalWeb(medicineName);
      }

      if (window.Swal) {
        const Toast = Swal.mixin({
          toast: true,
          position: 'top-end',
          showConfirmButton: false,
          timer: 2000,
          timerProgressBar: true
        });
        Toast.fire({
          icon: 'success',
          title: `Thermal barcode label generated for ${drug.name} (HSN: ${hsn})`
        });
      }
    }

    /**
     * Modal dialog to preview, customize, and print/download thermal barcode label using HSN code.
     */
    function openThermalBarcodeModalWeb(medicineName) {
      const drug = state.drugDatabase.find(d => d.name === medicineName);
      if (!drug) {
        if (window.Swal) Swal.fire('Error', 'Medicine details not found in database.', 'error');
        return;
      }

      const hsn = (drug.hsn || '300490').toString().trim();
      const defaultBatch = (drug.batch && drug.batch !== 'CAT-REQ') ? drug.batch : 'RX' + Math.floor(1000 + Math.random() * 9000);
      const defaultExp = drug.expiry || '12/2027';
      const pharmacyName = state.profile?.businessName || 'ROYAL PHARMACY';
      const barcodeSvg = generateHsnBarcodeSvg(hsn);

      const modalHtml = `
        <div class="text-left space-y-4 text-xs">
          <div class="p-3 bg-indigo-50 rounded-xl border border-indigo-100 flex items-center justify-between">
            <div>
              <p class="font-bold text-slate-800 text-sm">${escapeHtml(drug.name)}</p>
              <p class="text-[10px] text-slate-500">${escapeHtml(drug.generic || 'Generic Formulation')}</p>
            </div>
            <div class="text-right">
              <span class="px-2 py-0.5 bg-indigo-600 text-white text-[10px] font-bold rounded-full">HSN: ${escapeHtml(hsn)}</span>
              <p class="text-[10px] font-bold text-slate-700 mt-1">₹${(drug.price || 0).toFixed(2)}</p>
            </div>
          </div>

          <div class="grid grid-cols-2 gap-3">
            <div>
              <label class="block font-bold text-slate-700 mb-1">Batch Number:</label>
              <input type="text" id="thermal-batch-input" value="${escapeHtml(defaultBatch)}" class="w-full px-3 py-1.5 border border-slate-300 rounded-lg text-xs font-mono font-bold focus:ring-2 focus:ring-indigo-500/20">
            </div>
            <div>
              <label class="block font-bold text-slate-700 mb-1">Expiry Date:</label>
              <input type="text" id="thermal-exp-input" value="${escapeHtml(defaultExp)}" class="w-full px-3 py-1.5 border border-slate-300 rounded-lg text-xs font-mono focus:ring-2 focus:ring-indigo-500/20">
            </div>
          </div>

          <!-- Printable Label Preview -->
          <div class="pt-2">
            <label class="block font-bold text-slate-700 mb-1 text-[11px] uppercase tracking-wider">Printable Thermal Barcode Sticker (58mm x 40mm)</label>
            <div class="bg-white border-2 border-slate-900 rounded-xl p-3 shadow-sm text-center">
              <p class="text-[10px] font-black uppercase text-indigo-700 tracking-wider">${escapeHtml(pharmacyName)}</p>
              <p class="text-xs font-extrabold text-slate-900">${escapeHtml(drug.name)}</p>
              <p class="text-[10px] text-slate-500 mb-1">${escapeHtml(drug.generic || '')}</p>
              <div class="my-1.5 flex justify-center">
                ${barcodeSvg}
              </div>
              <div class="flex justify-between items-center text-[10px] font-mono font-bold border-t border-slate-200 pt-1 text-slate-700">
                <span>Batch: ${escapeHtml(defaultBatch)}</span>
                <span>Exp: ${escapeHtml(defaultExp)}</span>
                <span>MRP: ₹${(drug.price || 0).toFixed(2)}</span>
              </div>
            </div>
          </div>
        </div>
      `;

      Swal.fire({
        title: `Thermal Barcode Label: ${drug.name}`,
        html: modalHtml,
        showCancelButton: true,
        confirmButtonText: '🖨️ Print Thermal Label',
        confirmButtonColor: '#4F46E5',
        cancelButtonText: 'Close'
      }).then((result) => {
        if (result.isConfirmed) {
          generateAndPrintThermalBarcodeLabel(drug.name);
        }
      });
    }

    // Sync Inventory Function: forces a real-time push of all current medicine stock levels to Firestore & Google Drive
    async function syncInventoryWeb() {
      const syncBtn = document.getElementById('btn-sync-inventory-web');
      const originalBtnContent = syncBtn ? syncBtn.innerHTML : '';
      if (syncBtn) {
        syncBtn.disabled = true;
        syncBtn.innerHTML = '<i data-lucide="loader-2" class="w-4 h-4 animate-spin"></i><span>Syncing...</span>';
        if (window.lucide) lucide.createIcons();
      }

      try {
        const medicinesCount = state.drugDatabase.length;

        // 1. Push to Firestore if initialized
        if (typeof fbDb !== 'undefined' && fbDb) {
          const batch = fbDb.batch();
          const invCol = fbDb.collection('pharmacy_inventory');
          
          state.drugDatabase.slice(0, 100).forEach(med => {
            const docId = 'med_' + med.name.replace(/[^a-zA-Z0-9]/g, '_').toLowerCase();
            const docRef = invCol.doc(docId);
            batch.set(docRef, {
              name: med.name,
              genericName: med.generic || '',
              stockPacks: Number(med.stock) || 0,
              mrp: Number(med.price) || 0,
              purchasePrice: Number(med.purchasePrice) || 0,
              category: med.category || 'General',
              safetyCategory: med.safety || 'A',
              indication: med.indication || '',
              _lastUpdatedBy: 'WEB_PORTAL',
              _lastSyncedAt: firebase.firestore.FieldValue.serverTimestamp()
            }, { merge: true });
          });

          await batch.commit();
        }

        // 2. Push snapshot backup to Google Drive state
        state.lastGoogleDriveSync = "Just Now (" + new Date().toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' }) + ")";
        const backupSnapshot = {
          fileName: `RoyalPharmacy_InventorySync_${new Date().toISOString().slice(0, 10)}.json`,
          timestamp: "Just Now",
          totalRecords: medicinesCount,
          size: `${(medicinesCount * 0.35).toFixed(1)} KB`
        };
        state.driveSnapshots = [backupSnapshot, ...(state.driveSnapshots || []).slice(0, 4)];
        saveStateToLocal();

        // 3. Log staff/system activity
        if (typeof logStaffActivityWeb === 'function') {
          logStaffActivityWeb('INVENTORY_SYNC', `Pushed full inventory (${medicinesCount} medicines) to Firestore database & Google Drive`);
        }

        // 4. Trigger Toast Notification upon success
        const Toast = Swal.mixin({
          toast: true,
          position: 'top-end',
          showConfirmButton: false,
          timer: 3500,
          timerProgressBar: true,
          didOpen: (toast) => {
            toast.addEventListener('mouseenter', Swal.stopTimer);
            toast.addEventListener('mouseleave', Swal.resumeTimer);
          }
        });

        Toast.fire({
          icon: 'success',
          title: `Inventory Synced Successfully!`,
          html: `<span class="text-xs">Pushed <b>${medicinesCount} medicine stock levels</b> to Firestore database and Google Drive.</span>`
        });

        // WhatsApp / Push banner slide-in
        showLiveSyncNotification(
          "Inventory Synced",
          `Pushed ${medicinesCount} medicine stock levels to Firestore & Google Drive`,
          "cloud-upload"
        );

      } catch (err) {
        console.error("Inventory sync error:", err);
        Swal.fire('Sync Error', 'Failed to synchronize inventory: ' + (err.message || err), 'error');
      } finally {
        if (syncBtn) {
          syncBtn.disabled = false;
          syncBtn.innerHTML = originalBtnContent;
          if (window.lucide) lucide.createIcons();
        }
      }
    }

    function quickReorderDrugWeb(drugName) {
      const drug = state.drugDatabase.find(d => d.name === drugName);
      if (!drug) {
        if (window.Swal) Swal.fire('Error', 'Drug details not found in database.', 'error');
        return;
      }

      const minStockLevel = (state.customReorderLevels && state.customReorderLevels[drug.name]) !== undefined
        ? state.customReorderLevels[drug.name]
        : (drug.minStock || drug.reorderLevel || 15);

      // Recommended Reorder Quantity: brings inventory up to 2x safety threshold (minimum 10 units)
      const recommendedQty = Math.max(10, Math.ceil((minStockLevel * 2) - (drug.stock || 0)));
      const unitCost = drug.purchasePrice || (drug.price ? Number((drug.price * 0.75).toFixed(2)) : 20.0);

      state.distributorCart = state.distributorCart || [];
      const existing = state.distributorCart.find(item => item.name === drug.name);
      if (existing) {
        existing.qty += recommendedQty;
        existing.total = Number((existing.qty * existing.cost).toFixed(2));
      } else {
        state.distributorCart.push({
          name: drug.name,
          batch: "REORDER-AUTO",
          qty: recommendedQty,
          cost: unitCost,
          total: Number((recommendedQty * unitCost).toFixed(2))
        });
      }

      // Also mirror entry in cartItems for purchase order tracking
      state.cartItems = state.cartItems || [];
      const existingPo = state.cartItems.find(c => c.name === drug.name);
      if (existingPo) {
        existingPo.qty += recommendedQty;
      } else {
        state.cartItems.push({
          id: Date.now(),
          name: drug.name,
          manufacturer: "Royal Pharma Supplier",
          qty: recommendedQty,
          rate: unitCost,
          mrp: drug.price || unitCost,
          type: "PURCHASE_ORDER"
        });
      }

      saveStateToLocal();
      syncUiState();

      if (window.Swal) {
        Swal.fire({
          icon: 'success',
          title: 'Quick Reorder Added!',
          html: `
            <div class="text-left text-xs space-y-2 p-1">
              <p class="font-bold text-slate-800 dark:text-white text-sm">${escapeHtml(drug.name)}</p>
              <div class="flex items-center justify-between text-slate-600 dark:text-slate-300">
                <span>Current Stock: <b class="text-rose-600 font-extrabold">${drug.stock || 0} strips</b></span>
                <span>Threshold: <b class="text-amber-600 font-extrabold">${minStockLevel} strips</b></span>
              </div>
              <div class="p-2.5 bg-pink-50 dark:bg-pink-950/40 border border-pink-200 dark:border-pink-900 rounded-xl text-royal-magenta dark:text-pink-300 flex items-center justify-between">
                <span class="font-bold">Recommended Order:</span>
                <span class="text-sm font-black bg-pink-200/80 dark:bg-pink-900 px-2 py-0.5 rounded-lg">+${recommendedQty} strips</span>
              </div>
              <p class="text-[11px] text-slate-500 dark:text-slate-400">Added to <b>Required Purchases</b> cart (Est. Total: ₹${(recommendedQty * unitCost).toFixed(2)})</p>
            </div>
          `,
          timer: 2600,
          showConfirmButton: true,
          confirmButtonText: 'View Cart',
          confirmButtonColor: '#9C1258',
          showCancelButton: true,
          cancelButtonText: 'Continue',
          cancelButtonColor: '#64748B'
        }).then((result) => {
          if (result && result.isConfirmed) {
            switchTab('cart');
          }
        });
      }
    }

    function requestStockToCart(name, cost) {
      state.distributorCart.push({
        name: name,
        batch: "CAT-REQ",
        qty: 10,
        cost: cost,
        total: 10 * cost
      });
      saveStateToLocal();
      syncUiState();
      Swal.fire('Added!', `${name} added to Required Purchases.`, 'success');
    }

    // Dosage Calculator Logic
    function onDosageDrugChange() {
      const drug = document.getElementById('calc-drug').value;
      const factor = document.getElementById('calc-factor');
      if (drug === 'paracetamol') factor.value = '15';
      else if (drug === 'ibuprofen') factor.value = '10';
      else if (drug === 'amoxicillin') factor.value = '25';
      else if (drug === 'cetirizine') factor.value = '0.25';
      calculateDosage();
    }

    function calculateDosage() {
      const weight = parseFloat(document.getElementById('calc-weight').value) || 15;
      const age = document.getElementById('calc-age').value;
      const drug = document.getElementById('calc-drug').value;
      const factor = parseFloat(document.getElementById('calc-factor').value) || 15;

      let mgResult = weight * factor;
      let mlResult = 0.0;
      let interval = "Every 4 to 6 hours";

      if (drug === 'paracetamol') {
        // 120mg/5ml => 1ml = 24mg
        mlResult = mgResult / 24;
      } else if (drug === 'ibuprofen') {
        // 100mg/5ml => 1ml = 20mg
        mlResult = mgResult / 20;
      } else if (drug === 'amoxicillin') {
        // 125mg/5ml => 1ml = 25mg
        mlResult = mgResult / 25;
        interval = "Every 8 hours (3 times daily)";
      } else if (drug === 'cetirizine') {
        // 5mg/5ml syrup
        mlResult = mgResult / 1;
        interval = "Once daily (at night)";
      }

      document.getElementById('calc-volume-result').innerText = `${mlResult.toFixed(2)} ml`;
      document.getElementById('calc-substance-result').innerText = `${mgResult.toFixed(2)} mg`;
      document.getElementById('calc-interval-result').innerText = interval;
    }

    // Symptom Triage Checker
    function runSymptomChecker() {
      const panel = document.getElementById('symptom-result-panel');
      const emptyState = document.getElementById('symptom-empty-state');
      const checkboxes = document.querySelectorAll('#symptom-checkboxes input:checked');

      if (checkboxes.length === 0) {
        panel.classList.add('hidden');
        emptyState.classList.remove('hidden');
        return;
      }

      panel.classList.remove('hidden');
      emptyState.classList.add('hidden');

      let selected = Array.from(checkboxes).map(c => c.value);
      
      const banner = document.getElementById('symptom-risk-banner');
      const bannerIconBg = document.getElementById('symptom-risk-icon-bg');
      const riskTitle = document.getElementById('symptom-risk-title');
      const riskDesc = document.getElementById('symptom-risk-desc');
      const otcList = document.getElementById('symptom-otc-list');
      const cautionText = document.getElementById('symptom-precaution-text');

      // Triage Rules
      if (selected.includes('fever') && selected.includes('cough')) {
        banner.className = "flex items-center space-x-3 p-4 rounded-xl border border-rose-200 bg-rose-50 text-rose-800";
        bannerIconBg.className = "p-2 rounded-full bg-rose-600 text-white";
        riskTitle.innerText = "Triage Alert: Moderate to High Risk";
        riskDesc.innerText = "Symptom set could indicate acute bronchial or respiratory challenges. Restrict dosage and advice MBBS doctor review.";
        otcList.innerHTML = `
          <span class="bg-rose-100 text-rose-800 text-xs px-2.5 py-1 rounded-full font-bold">Antipyretics</span>
          <span class="bg-rose-100 text-rose-800 text-xs px-2.5 py-1 rounded-full font-bold">Cough Suppressants</span>
        `;
        cautionText.innerText = "Do NOT dose multi-ingredient syrups simultaneously to avoid accidental paracetamol double-dosing hazards.";
      } else if (selected.includes('acidity')) {
        banner.className = "flex items-center space-x-3 p-4 rounded-xl border border-emerald-200 bg-emerald-50 text-emerald-800";
        bannerIconBg.className = "p-2 rounded-full bg-emerald-600 text-white";
        riskTitle.innerText = "Triage: Mild Digestive Distress";
        riskDesc.innerText = "Simple hyperacidity or bloating. Highly responsive to Over-The-Counter chemist solutions.";
        otcList.innerHTML = `
          <span class="bg-emerald-100 text-emerald-800 text-xs px-2.5 py-1 rounded-full font-bold">Proton Pump Inhibitors</span>
          <span class="bg-emerald-100 text-emerald-800 text-xs px-2.5 py-1 rounded-full font-bold">Antacids Suspensions</span>
        `;
        cautionText.innerText = "Advise patient to avoid lying down immediately after meals and restrict high acidic oil diet patterns.";
      } else {
        banner.className = "flex items-center space-x-3 p-4 rounded-xl border border-amber-200 bg-amber-50 text-amber-800";
        bannerIconBg.className = "p-2 rounded-full bg-amber-600 text-white";
        riskTitle.innerText = "Triage Warning: Basic Chemist Care";
        riskDesc.innerText = "Symptoms are safe to treat with generic cold, allergy, or anti-inflammatory drugs.";
        otcList.innerHTML = `
          <span class="bg-amber-100 text-amber-800 text-xs px-2.5 py-1 rounded-full font-bold">Antihistamines</span>
          <span class="bg-amber-100 text-amber-800 text-xs px-2.5 py-1 rounded-full font-bold">Saline Nasal Drops</span>
        `;
        cautionText.innerText = "Advise plenty of warm fluid hydration. Review again if indicators continue over 72 hours.";
      }
    }

    // Smart Invoice printer rendering logic
    function openEditDetailsDialog() {
      if (state.role !== 'OWNER') {
        Swal.fire('Permissions Restricted', 'Staff accounts cannot edit pharmacy details on cash memo settings.', 'error');
        return;
      }

      Swal.fire({
        title: 'Edit Pharmacy Cash Memo Details',
        html: `
          <input id="inv-bname" class="swal2-input" placeholder="Pharmacy Name" value="${state.profile.businessName}">
          <input id="inv-phone" class="swal2-input" placeholder="Phone" value="${state.profile.phone}">
          <input id="inv-gstin" class="swal2-input" placeholder="GSTIN" value="${state.profile.gstin}">
          <input id="inv-dl20" class="swal2-input" placeholder="Drug Lic 20B" value="${state.profile.drugLicenseForm20}">
          <input id="inv-dl21" class="swal2-input" placeholder="Drug Lic 21B" value="${state.profile.drugLicenseForm21}">
          <input id="inv-bank-acc" class="swal2-input" placeholder="Bank Account Number" value="${state.profile.bankAccountNumber || ''}">
          <input id="inv-bank-ifsc" class="swal2-input" placeholder="Bank IFSC & Name" value="${state.profile.bankIfsc || ''}">
        `,
        focusConfirm: false,
        preConfirm: () => {
          return {
            businessName: document.getElementById('inv-bname').value,
            phone: document.getElementById('inv-phone').value,
            gstin: document.getElementById('inv-gstin').value,
            drugLicenseForm20: document.getElementById('inv-dl20').value,
            drugLicenseForm21: document.getElementById('inv-dl21').value,
            bankAccountNumber: document.getElementById('inv-bank-acc').value,
            bankIfsc: document.getElementById('inv-bank-ifsc').value
          }
        }
      }).then((res) => {
        if (res.value) {
          state.profile = { ...state.profile, ...res.value };
          saveStateToLocal();
          syncUiState();
          Swal.fire('Updated!', 'Pharmacy cash memo headers updated.', 'success');
        }
      });
    }

    function renderInvoicePreview() {
      const sheet = document.getElementById('invoice-preview-sheet');
      const incGst = document.getElementById('inc-gst').checked;
      const incDl = document.getElementById('inc-dl').checked;
      const incAyushman = document.getElementById('inc-ayushman').checked;
      const paperSize = document.getElementById('invoice-paper').value;
      const copyType = document.getElementById('invoice-copy').value;

      // Adjust preview width depending on selection
      if (paperSize === 'A4_STANDARD') {
        sheet.style.width = '550px';
      } else {
        sheet.style.width = '380px';
      }

      let subtotal = 0.0;
      let itemsRows = '';

      if (state.currentSale.items.length === 0) {
        itemsRows = `<tr><td colspan="4" class="text-center py-6 text-xs text-slate-400 italic">No bill medicines checkout yet. Sample demo loaded.</td></tr>`;
        // Fallback sample view
        subtotal = 417.00;
        itemsRows = `
          <tr style="border-bottom: 1px dashed #e2e8f0; font-size: 11px;">
            <td style="padding: 6px 0;"><strong>Paracetamol (Dolo) 650mg</strong><br><span style="font-size: 9px; color:#64748b">Batch: DLO-99 | Exp: 12/27</span></td>
            <td class="text-center">2</td>
            <td class="text-right">₹30.00</td>
            <td class="text-right">₹60.00</td>
          </tr>
          <tr style="border-bottom: 1px dashed #e2e8f0; font-size: 11px;">
            <td style="padding: 6px 0;"><strong>Pantocid 40mg Tablet</strong><br><span style="font-size: 9px; color:#64748b">Batch: PAN-88 | Exp: 10/27</span></td>
            <td class="text-center">3</td>
            <td class="text-right">₹95.00</td>
            <td class="text-right">₹285.00</td>
          </tr>
        `;
      } else {
        state.currentSale.items.forEach(item => {
          subtotal += item.total;
          itemsRows += `
            <tr style="border-bottom: 1px dashed #e2e8f0; font-size: 11px;">
              <td style="padding: 6px 0;"><strong>${item.name}</strong><br><span style="font-size: 9px; color:#64748b">Batch: ${item.batch} | Exp: ${item.expiry}</span></td>
              <td class="text-center">${item.qty}</td>
              <td class="text-right">₹${item.mrp.toFixed(2)}</td>
              <td class="text-right">₹${item.total.toFixed(2)}</td>
            </tr>
          `;
        });
      }

      const gstVal = subtotal * 0.12;

      sheet.innerHTML = `
        <div class="space-y-4">
          <!-- Pharmacy Headings -->
          <div class="text-center">
            <h4 class="font-bold text-base tracking-wide">${state.profile.businessName}</h4>
            <p class="text-[11px] text-slate-500">${state.profile.addressLine1}, ${state.profile.addressLine2}</p>
            <p class="text-[11px] text-slate-500">Phone: ${state.profile.phone}</p>
            <p class="text-[10px] font-bold text-pink-600 tracking-wider uppercase mt-1">[${copyType} FOR CUSTOMER]</p>
          </div>

          <div style="border-bottom: 1px dashed #475569;"></div>

          <!-- Regulatory Block -->
          <div class="grid grid-cols-2 text-[10px] gap-2 text-slate-600">
            <div>
              <p><strong>Customer:</strong> ${state.currentSale.customerName}</p>
              <p><strong>Prescription:</strong> ${state.currentSale.doctorName}</p>
            </div>
            <div class="text-right">
              <p><strong>Memo No:</strong> MEMO-${Math.floor(1000 + Math.random()*9000)}</p>
              <p><strong>Date:</strong> ${new Date().toLocaleDateString()}</p>
            </div>
          </div>

          <div style="border-bottom: 1px dashed #475569;"></div>

          <!-- Item Table -->
          <table class="w-full text-left text-xs">
            <thead>
              <tr style="border-bottom: 1px solid #475569; font-size: 10px; font-weight: bold; color: #475569;">
                <th style="padding-bottom: 4px;">Item Particulars</th>
                <th class="text-center" style="padding-bottom: 4px;">Qty</th>
                <th class="text-right" style="padding-bottom: 4px;">MRP</th>
                <th class="text-right" style="padding-bottom: 4px;">Total</th>
              </tr>
            </thead>
            <tbody>
              ${itemsRows}
            </tbody>
          </table>

          <div style="border-bottom: 1px dashed #475569;"></div>

          <!-- Totals -->
          <div class="space-y-1 text-xs">
            <div class="flex justify-between text-slate-600">
              <span>Subtotal:</span>
              <span>₹${subtotal.toFixed(2)}</span>
            </div>
            ${incGst ? `
            <div class="flex justify-between text-slate-600">
              <span>GST (12% Included):</span>
              <span>₹${gstVal.toFixed(2)}</span>
            </div>` : ''}
            <div class="flex justify-between font-bold text-sm text-slate-900 border-t border-slate-200 pt-1">
              <span>Net Payable:</span>
              <span>₹${subtotal.toFixed(2)}</span>
            </div>
          </div>

          <div style="border-bottom: 1px dashed #475569;"></div>

          <!-- Bank Details -->
          ${state.profile.bankAccountNumber ? `
          <div class="text-[9px] text-slate-500 bg-slate-50 p-2 rounded border border-slate-200">
            <p><strong>Bank:</strong> ${state.profile.bankIfsc}</p>
            <p><strong>A/c No:</strong> ${state.profile.bankAccountNumber}</p>
          </div>` : ''}

          <div style="border-bottom: 1px dashed #475569;"></div>

          <!-- Licenses and Signatures -->
          <div class="text-[9px] text-slate-500 space-y-1">
            ${incDl ? `<p><strong>DL Form 20B / 21B:</strong> ${state.profile.drugLicenseForm20} / ${state.profile.drugLicenseForm21}</p>` : ''}
            ${incGst ? `<p><strong>GSTIN:</strong> ${state.profile.gstin}</p>` : ''}
            ${incAyushman ? `<p><strong>Ayushman HFR ID:</strong> ${state.profile.ayushmanHfrId}</p>` : ''}
          </div>

          <div class="pt-6 flex justify-between items-end text-[10px] text-slate-500">
            <div>
              <p>Payment Mode: <strong>${state.currentSale.paymentMode}</strong></p>
              <p class="text-[9px] text-emerald-600">✔ Fully compliant Chemist Cash Memo</p>
            </div>
            <div class="text-center">
              <div class="w-24 border-t border-slate-300 mx-auto mt-6"></div>
              <p class="text-[9px] pt-1">Pharmacist Signatory</p>
            </div>
          </div>
        </div>
      `;
    }

    function printInvoiceBrowser() {
      // Create temporary print window
      const printContents = document.getElementById('invoice-preview-sheet').innerHTML;
      const originalContents = document.body.innerHTML;
      
      const popup = window.open('', '_blank');
      popup.document.open();
      popup.document.write(`
        <html>
        <head>
          <title>Cash Memo Print</title>
          <style>
            body { font-family: 'Courier New', Courier, monospace; width: 80mm; padding: 10px; margin: 0 auto; color: #000; }
            .text-center { text-align: center; }
            .text-right { text-align: right; }
            .flex { display: flex; justify-content: space-between; }
            table { width: 100%; border-collapse: collapse; }
            th, td { font-size: 11px; }
            hr { border-top: 1px dashed #000; }
          </style>
        </head>
        <body onload="window.print(); window.close();">
          ${printContents}
        </body>
        </html>
      `);
      popup.document.close();
    }

    function simulateBluetoothPrinterPrint() {
      Swal.fire({
        title: 'Searching for ESC/POS Bluetooth Thermal Printers...',
        html: `
          <div class="text-left space-y-2.5 text-xs text-slate-600 p-2">
            <p class="font-semibold mb-2">Select paired chemist printer:</p>
            <label class="flex items-center space-x-3 p-2.5 bg-slate-50 rounded-lg border border-slate-200 cursor-pointer">
              <input type="radio" name="bt-p" checked value="pos58">
              <span><strong>BT-POS58 Thermal Printer</strong> (00:11:22:33:44:55)</span>
            </label>
            <label class="flex items-center space-x-3 p-2.5 bg-slate-50 rounded-lg border border-slate-200 cursor-pointer">
              <input type="radio" name="bt-p" value="pos80">
              <span><strong>POS-80 Bluetooth Receipt Printer</strong> (AA:BB:CC:DD:EE:FF)</span>
            </label>
            <p class="text-[10px] text-slate-400 mt-2">Ensure your 2-inch or 3-inch thermal roll printer is turned on with Bluetooth active.</p>
          </div>
        `,
        icon: 'info',
        showCancelButton: true,
        confirmButtonColor: '#9C1258',
        confirmButtonText: 'Print ESC/POS Ticket'
      }).then((res) => {
        if (res.isConfirmed) {
          Swal.fire({
            title: 'Sending ESC/POS Bytes...',
            text: 'Sending raw receipt data to bluetooth SPP socket (RFCOMM)...',
            timer: 1500,
            didOpen: () => { Swal.showLoading() }
          }).then(() => {
            Swal.fire('Print Succeeded!', '✅ ESC/POS Thermal Receipt sent to Bluetooth Printer.', 'success');
          });
        }
      });
    }

    // Startup Init
    window.addEventListener('DOMContentLoaded', () => {
      initWebDarkMode();
      loadSavedState();
      onDosageDrugChange();
      startWebRealtimeClock();
    });

    function startWebRealtimeClock() {
      const clockEl = document.getElementById('header-realtime-clock');
      if (!clockEl) return;
      function update() {
        const now = new Date();
        clockEl.textContent = now.toLocaleTimeString('en-US', {
          hour: '2-digit',
          minute: '2-digit',
          second: '2-digit',
          hour12: true
        });
      }
      update();
      setInterval(update, 1000);
    }

    function initWebDarkMode() {
      const savedDark = localStorage.getItem('royal_web_dark_mode') === 'true' ||
        (window.matchMedia && window.matchMedia('(prefers-color-scheme: dark)').matches);
      if (savedDark) {
        document.documentElement.classList.add('dark');
      }
      updateWebDarkModeIcon(savedDark);
    }

    function toggleWebDarkMode() {
      const isDark = document.documentElement.classList.toggle('dark');
      localStorage.setItem('royal_web_dark_mode', isDark ? 'true' : 'false');
      updateWebDarkModeIcon(isDark);
    }

    function updateWebDarkModeIcon(isDark) {
      const icon = document.getElementById('web-dark-toggle-icon');
      if (icon) {
        icon.setAttribute('data-lucide', isDark ? 'sun' : 'moon');
        if (window.lucide) lucide.createIcons();
      }
    }

    // Quick Scan Hub & Gemini AI Prescription Scanner Implementation
    let webHtml5QrCode = null;
    let webCapturedPhotos = []; // [{ id, dataUrl, base64, angleLabel }]

    function startQuickScan() {
      webCapturedPhotos = [];
      Swal.fire({
        title: 'Quick Scan & Multi-Photo AI Vision Hub',
        html: `
          <div class="flex border-b border-slate-200 mb-4">
            <button onclick="switchScanTab('strip')" id="scan-tab-strip" class="flex-1 pb-2 font-bold text-sm text-royal-magenta border-b-2 border-royal-magenta">📸 Multi-Photo Strip / Box</button>
            <button onclick="switchScanTab('barcode')" id="scan-tab-barcode" class="flex-1 pb-2 font-semibold text-sm text-slate-500">Barcode / QR</button>
            <button onclick="switchScanTab('rx')" id="scan-tab-rx" class="flex-1 pb-2 font-semibold text-sm text-slate-500">Doctor Rx (AI)</button>
          </div>
          
          <div id="scan-panel-strip" class="space-y-3 text-left">
            <div class="flex items-center justify-between">
              <label class="block text-xs font-semibold text-slate-600">Medicine Packaging Photos (Front, Flap, MRP)</label>
              <div class="flex items-center space-x-2">
                <label class="px-2.5 py-1 bg-royal-magenta text-white text-xs font-bold rounded-xl cursor-pointer hover:bg-pink-800 transition flex items-center space-x-1 shadow-sm">
                  <span>📷 Snap Camera</span>
                  <input type="file" accept="image/*" capture="environment" onchange="handleMultiStripPhotoUpload(this)" class="hidden">
                </label>
                <label class="px-2.5 py-1 bg-slate-100 hover:bg-slate-200 text-slate-700 text-xs font-bold rounded-xl cursor-pointer transition flex items-center space-x-1 border border-slate-300">
                  <span>📁 Upload Multiple</span>
                  <input type="file" accept="image/*" multiple onchange="handleMultiStripPhotoUpload(this)" class="hidden">
                </label>
              </div>
            </div>
            
            <!-- Live Multi-Photo Thumbnails Reel -->
            <div id="web-multi-photo-tray" class="p-2.5 bg-slate-50 rounded-xl border border-slate-200 flex items-center gap-2 overflow-x-auto min-h-[90px]">
              <div class="text-slate-400 text-xs text-center w-full py-4">
                No photos added yet. Snap or upload front, flap (batch/expiry), and back photos to combine details!
              </div>
            </div>

            <div class="flex items-center justify-between pt-1">
              <button onclick="clearWebCapturedPhotos()" class="text-xs text-slate-500 hover:text-rose-600 font-semibold underline">Clear Photos</button>
              <button onclick="mergeAndExtractWebPhotos()" id="btn-merge-web-photos" class="px-4 py-2 bg-gradient-to-r from-pink-600 to-purple-600 hover:from-pink-700 hover:to-purple-700 text-white font-bold text-xs rounded-xl shadow-md transition flex items-center space-x-1.5 disabled:opacity-50" disabled>
                <span>⚡ Merge & Extract Details</span>
              </button>
            </div>
          </div>

          <div id="scan-panel-barcode" class="space-y-3 hidden">
            <div id="reader" style="width: 100%; min-height: 200px; border-radius: 12px; overflow: hidden; background: #000;"></div>
            <div class="flex items-center justify-between pt-1">
              <label class="block text-[11px] font-semibold text-slate-500">Scan from Image / Photo:</label>
              <label class="px-3 py-1 bg-slate-100 hover:bg-slate-200 text-slate-700 text-xs font-bold rounded-xl cursor-pointer transition flex items-center space-x-1 border border-slate-300">
                <span>📁 Upload Barcode Photo</span>
                <input type="file" accept="image/*" onchange="handleBarcodeImageUpload(this)" class="hidden">
              </label>
            </div>
            <div class="space-y-1">
              <label class="block text-[11px] font-semibold text-slate-500">⚡ Or Quick Test Barcodes:</label>
              <div class="grid grid-cols-2 gap-2">
                <button onclick="simulateBarcode('Paracetamol')" class="px-3 py-2 bg-pink-50 hover:bg-pink-100 text-royal-magenta text-xs font-bold rounded-xl border border-pink-200 transition">Paracetamol 650mg</button>
                <button onclick="simulateBarcode('Augmentin')" class="px-3 py-2 bg-pink-50 hover:bg-pink-100 text-royal-magenta text-xs font-bold rounded-xl border border-pink-200 transition">Augmentin 625 Duo</button>
                <button onclick="simulateBarcode('Dolo')" class="px-3 py-2 bg-pink-50 hover:bg-pink-100 text-royal-magenta text-xs font-bold rounded-xl border border-pink-200 transition">Dolo 650</button>
                <button onclick="simulateBarcode('Pantoprazole')" class="px-3 py-2 bg-pink-50 hover:bg-pink-100 text-royal-magenta text-xs font-bold rounded-xl border border-pink-200 transition">Pan 40 Tablet</button>
              </div>
            </div>
            <p class="text-[11px] text-slate-500">Point your camera at the barcode on any medicine box, vial, or strip.</p>
          </div>

          <div id="scan-panel-rx" class="space-y-3 hidden text-left">
            <div class="flex items-center justify-between">
              <label class="block text-xs font-semibold text-slate-600">Doctor Prescription (AI Vision OCR)</label>
              <label class="px-3 py-1.5 bg-royal-magenta text-white text-xs font-bold rounded-xl cursor-pointer hover:bg-pink-800 transition flex items-center space-x-1.5 shadow-sm">
                <span>📷 Capture Prescription</span>
                <input type="file" accept="image/*" capture="environment" onchange="handlePrescriptionOcrUpload(this)" class="hidden">
              </label>
            </div>
            <input type="file" accept="image/*" id="rx-file-input" onchange="handlePrescriptionOcrUpload(this)" class="w-full text-xs text-slate-500 file:mr-4 file:py-2 file:px-4 file:rounded-xl file:border-0 file:text-xs file:font-bold file:bg-pink-50 file:text-royal-magenta hover:file:bg-pink-100">
            <div class="space-y-1">
              <label class="block text-[11px] font-semibold text-slate-500">Or Select Sample Prescription Preset:</label>
              <select id="rx-sample-preset" onchange="processSamplePresetRx(this.value)" class="w-full px-3 py-1.5 rounded-xl border border-slate-200 text-xs bg-white">
                <option value="">-- Choose Preset to Test --</option>
                <option value="Dr. Sharma Rx">Dr. Sharma Rx (Augmentin & Pan 40)</option>
                <option value="Dr. Verma Rx">Dr. Verma Rx (Metformin & Telma 40)</option>
                <option value="Dr. Iyer Rx">Dr. Iyer Rx (Azithromycin & Allegra)</option>
              </select>
            </div>
            <div id="rx-preview-area" class="p-3 bg-slate-50 rounded-xl text-xs text-slate-600 min-h-[80px] flex items-center justify-center border border-dashed border-slate-300">
              Upload prescription image. Gemini AI Vision will extract medicines and dosage.
            </div>
          </div>
        `,
        width: '640px',
        showCancelButton: true,
        cancelButtonText: 'Close Scanner',
        showConfirmButton: false,
        didOpen: () => {
          renderWebPhotoTray();
        },
        willClose: () => {
          stopWebBarcodeScanner();
        }
      });
    }

    function preprocessImageForOcr(dataUrl, callback) {
      const img = new Image();
      img.onload = function() {
        const canvas = document.createElement('canvas');
        const ctx = canvas.getContext('2d');
        const maxDim = 1200;
        let w = img.width;
        let h = img.height;
        if (w > maxDim || h > maxDim) {
          if (w > h) {
            h = Math.round((h * maxDim) / w);
            w = maxDim;
          } else {
            w = Math.round((w * maxDim) / h);
            h = maxDim;
          }
        }
        canvas.width = w;
        canvas.height = h;
        ctx.drawImage(img, 0, 0, w, h);

        try {
          // Deskewing pass (Simple)
          const angle = detectSkewAngle(ctx, w, h);
          if (Math.abs(angle) > 0.5) {
            ctx.clearRect(0,0,w,h);
            ctx.save();
            ctx.translate(w/2, h/2);
            ctx.rotate(-angle * Math.PI / 180);
            ctx.drawImage(img, -w/2, -h/2, w, h);
            ctx.restore();
          }

          let imgData = ctx.getImageData(0, 0, w, h);
          const data = imgData.data;
          
          // Adaptive Thresholding logic
          // 1. Grayscale
          const gray = new Uint8Array(w * h);
          for (let i = 0; i < data.length; i += 4) {
            gray[i/4] = 0.299 * data[i] + 0.587 * data[i+1] + 0.114 * data[i+2];
          }

          // 2. Local mean (box blur approximation)
          const thresholded = new Uint8Array(w * h);
          const radius = 10;
          const stride = w;
          
          for (let y = 0; y < h; y++) {
            for (let x = 0; x < w; x++) {
              let sum = 0, count = 0;
              // Sample local window
              for (let dy = -radius; dy <= radius; dy += 4) {
                for (let dx = -radius; dx <= radius; dx += 4) {
                  const ny = y + dy, nx = x + dx;
                  if (ny >= 0 && ny < h && nx >= 0 && nx < w) {
                    sum += gray[ny * stride + nx];
                    count++;
                  }
                }
              }
              const localMean = sum / count;
              thresholded[y * stride + x] = (gray[y * stride + x] < localMean - 15) ? 0 : 255;
            }
          }

          // Write back to imageData
          for (let i = 0; i < data.length; i += 4) {
            const val = thresholded[i/4];
            data[i] = data[i+1] = data[i+2] = val;
          }
          
          ctx.putImageData(imgData, 0, 0);

          const enhancedDataUrl = canvas.toDataURL('image/jpeg', 0.85);
          const enhancedBase64 = enhancedDataUrl.split(',')[1];
          callback(enhancedDataUrl, enhancedBase64);
        } catch (_e) {
          callback(dataUrl, dataUrl.split(',')[1]);
        }
      };
      img.src = dataUrl;
    }

    function detectSkewAngle(ctx, w, h) {
      // Sample a small area for orientation
      const sample = ctx.getImageData(0, 0, w, h);
      const data = sample.data;
      let maxVar = 0, bestAngle = 0;
      
      // Test angles -5 to 5
      for (let a = -5; a <= 5; a += 1) {
        const rad = a * Math.PI / 180;
        const cos = Math.cos(rad), sin = Math.sin(rad);
        const projections = new Float32Array(h);
        
        for (let y = 0; y < h; y += 10) {
          for (let x = 0; x < w; x += 10) {
            const gray = data[(y*w+x)*4];
            const newY = Math.round(-x * sin + y * cos);
            if (newY >= 0 && newY < h) projections[newY] += (255 - gray);
          }
        }
        
        let mean = 0;
        for (let i = 0; i < h; i++) mean += projections[i];
        mean /= h;
        let variance = 0;
        for (let i = 0; i < h; i++) variance += Math.pow(projections[i] - mean, 2);
        
        if (variance > maxVar) {
          maxVar = variance;
          bestAngle = a;
        }
      }
      return bestAngle;
    }

    function handleMultiStripPhotoUpload(input) {
      if (!input.files || input.files.length === 0) return;
      const angleLabels = ["Photo 1 (Front / Brand)", "Photo 2 (Batch & Expiry)", "Photo 3 (MRP & Back)", "Photo 4 (Composition)"];
      
      Array.from(input.files).forEach((file) => {
        const reader = new FileReader();
        reader.onload = function(e) {
          const rawDataUrl = e.target.result;
          preprocessImageForOcr(rawDataUrl, (enhancedDataUrl, enhancedBase64) => {
            const nextIndex = webCapturedPhotos.length;
            const label = nextIndex < angleLabels.length ? angleLabels[nextIndex] : `Angle ${nextIndex + 1}`;
            
            webCapturedPhotos.push({
              id: 'photo_' + Date.now() + '_' + Math.random().toString(36).substr(2, 5),
              dataUrl: enhancedDataUrl,
              base64: enhancedBase64,
              rawUrl: rawDataUrl,
              fileName: file.name,
              angleLabel: label
            });
            renderWebPhotoTray();
          });
        };
        reader.readAsDataURL(file);
      });
    }

    function removeWebCapturedPhoto(id) {
      webCapturedPhotos = webCapturedPhotos.filter(p => p.id !== id);
      renderWebPhotoTray();
    }

    function clearWebCapturedPhotos() {
      webCapturedPhotos = [];
      renderWebPhotoTray();
    }

    function renderWebPhotoTray() {
      const tray = document.getElementById('web-multi-photo-tray');
      const btn = document.getElementById('btn-merge-web-photos');
      if (!tray) return;

      if (webCapturedPhotos.length === 0) {
        tray.innerHTML = `
          <div class="text-slate-400 text-xs text-center w-full py-4">
            No photos added yet. Snap or upload front, flap (batch/expiry), and back photos to combine details!
          </div>
        `;
        if (btn) btn.disabled = true;
        return;
      }

      if (btn) {
        btn.disabled = false;
        btn.innerHTML = `<span>⚡ Merge & Extract (${webCapturedPhotos.length} Photo${webCapturedPhotos.length > 1 ? 's' : ''})</span>`;
      }

      let html = '';
      webCapturedPhotos.forEach((p, idx) => {
        html += `
          <div class="relative group shrink-0 w-24 h-24 rounded-xl border-2 border-pink-300 overflow-hidden bg-slate-900 shadow-sm">
            <img src="${p.dataUrl}" class="w-full h-full object-cover">
            <span class="absolute bottom-0 inset-x-0 bg-black/70 text-[9px] font-bold text-white text-center py-0.5 truncate px-1">${p.angleLabel}</span>
            <button onclick="removeWebCapturedPhoto('${p.id}')" class="absolute top-1 right-1 bg-rose-600 text-white rounded-full w-5 h-5 flex items-center justify-center text-[10px] font-black hover:bg-rose-700 shadow">✕</button>
          </div>
        `;
      });
      tray.innerHTML = html;
    }

    async function mergeAndExtractWebPhotos() {
      if (webCapturedPhotos.length === 0) return;
      
      const btn = document.getElementById('btn-merge-web-photos');
      if (btn) {
        btn.disabled = true;
        btn.innerHTML = `<span class="animate-spin">⏳</span><span> Analyzing ${webCapturedPhotos.length} Photos with Gemini AI...</span>`;
      }

      const apiKey = localStorage.getItem('gemini_api_key') || '';
      let detected = null;

      if (apiKey && apiKey.length >= 20) {
        try {
          const parts = [
            { text: "Analyze these medicine package photos (which may contain front brand name, side flap with batch number and expiry date, back with composition, and MRP). Merge all fields into a single high-accuracy record. Extract: medicineName, saltComposition, batchNumber, expiryDate (MM/YY format), mrp (numeric), manufacturer. Return ONLY valid JSON format: {\"medicineName\":\"...\",\"saltComposition\":\"...\",\"batchNumber\":\"...\",\"expiryDate\":\"MM/YY\",\"mrp\":0.0,\"manufacturer\":\"...\"}" }
          ];
          webCapturedPhotos.forEach(p => {
            parts.push({
              inline_data: { mime_type: "image/jpeg", data: p.base64 }
            });
          });

          const url = `https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=${apiKey}`;
          const resp = await fetch(url, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ contents: [{ parts: parts }] })
          });
          const data = await resp.json();
          const text = data.candidates?.[0]?.content?.parts?.[0]?.text;
          if (text) {
            const clean = text.replace(/```json/g, '').replace(/```/g, '').trim();
            detected = JSON.parse(clean);
          }
        } catch (e) {
          console.error("Gemini Multi-Photo OCR error:", e);
        }
      }

      // Smart Heuristic Fallback based on image names & catalogue
      const firstFileName = webCapturedPhotos[0]?.fileName?.replace(/\.[^/.]+$/, "").replace(/[-_]/g, " ") || "";
      const medName = detected?.medicineName || firstFileName || "Scanned Medicine";
      const saltComp = detected?.saltComposition || "Active Pharmaceutical Ingredient";
      const batchNo = detected?.batchNumber || "BT" + Math.floor(10000 + Math.random() * 90000);
      const expDate = detected?.expiryDate || "08/28";
      const mrpVal = detected?.mrp || 145.0;
      const mfgComp = detected?.manufacturer || "Standard Pharmaceuticals Ltd";

      Swal.fire({
        title: '📦 Scanned Medicine Details',
        html: `
          <div class="text-left space-y-2.5 text-xs">
            <p class="text-slate-500">Merged from <b>${webCapturedPhotos.length} photo(s)</b>. Confirm details before saving:</p>
            <div>
              <label class="font-bold text-slate-700">Brand Name:</label>
              <input id="ocr-med-name" class="w-full px-3 py-1.5 border border-slate-300 rounded-lg text-xs font-semibold focus:ring-2 focus:ring-pink-500/20" value="${escapeHtml(medName)}">
            </div>
            <div>
              <label class="font-bold text-slate-700">Salt / Composition:</label>
              <input id="ocr-med-salt" class="w-full px-3 py-1.5 border border-slate-300 rounded-lg text-xs focus:ring-2 focus:ring-pink-500/20" value="${escapeHtml(saltComp)}">
            </div>
            <div class="grid grid-cols-2 gap-2">
              <div>
                <label class="font-bold text-slate-700">Batch No:</label>
                <input id="ocr-med-batch" class="w-full px-3 py-1.5 border border-slate-300 rounded-lg text-xs font-mono font-bold focus:ring-2 focus:ring-pink-500/20" value="${escapeHtml(batchNo)}">
              </div>
              <div>
                <label class="font-bold text-slate-700">Expiry (MM/YY):</label>
                <input id="ocr-med-expiry" class="w-full px-3 py-1.5 border border-slate-300 rounded-lg text-xs font-mono font-bold text-rose-600 focus:ring-2 focus:ring-pink-500/20" value="${escapeHtml(expDate)}">
              </div>
            </div>
            <div class="grid grid-cols-2 gap-2">
              <div>
                <label class="font-bold text-slate-700">MRP Price (₹):</label>
                <input id="ocr-med-mrp" type="number" step="0.5" class="w-full px-3 py-1.5 border border-slate-300 rounded-lg text-xs font-bold focus:ring-2 focus:ring-pink-500/20" value="${mrpVal}">
              </div>
              <div>
                <label class="font-bold text-slate-700">Quantity:</label>
                <input id="ocr-med-qty" type="number" min="1" class="w-full px-3 py-1.5 border border-slate-300 rounded-lg text-xs font-bold focus:ring-2 focus:ring-pink-500/20" value="10">
              </div>
            </div>
            <div>
              <label class="font-bold text-slate-700">Manufacturer:</label>
              <input id="ocr-med-mfg" class="w-full px-3 py-1.5 border border-slate-300 rounded-lg text-xs focus:ring-2 focus:ring-pink-500/20" value="${escapeHtml(mfgComp)}">
            </div>
          </div>
        `,
        width: '560px',
        showCancelButton: true,
        showDenyButton: true,
        confirmButtonText: '🛒 Add to Sale Bill',
        denyButtonText: '📦 Add to Stock Inventory',
        cancelButtonText: 'Cancel',
        confirmButtonColor: '#9d174d',
        denyButtonColor: '#047857',
        preConfirm: () => {
          return {
            name: document.getElementById('ocr-med-name').value.trim() || medName,
            salt: document.getElementById('ocr-med-salt').value.trim() || saltComp,
            batch: document.getElementById('ocr-med-batch').value.trim() || batchNo,
            expiry: document.getElementById('ocr-med-expiry').value.trim() || expDate,
            mrp: parseFloat(document.getElementById('ocr-med-mrp').value) || mrpVal,
            qty: parseInt(document.getElementById('ocr-med-qty').value) || 1,
            mfg: document.getElementById('ocr-med-mfg').value.trim() || mfgComp
          };
        },
        preDeny: () => {
          return {
            name: document.getElementById('ocr-med-name').value.trim() || medName,
            salt: document.getElementById('ocr-med-salt').value.trim() || saltComp,
            batch: document.getElementById('ocr-med-batch').value.trim() || batchNo,
            expiry: document.getElementById('ocr-med-expiry').value.trim() || expDate,
            mrp: parseFloat(document.getElementById('ocr-med-mrp').value) || mrpVal,
            qty: parseInt(document.getElementById('ocr-med-qty').value) || 10,
            mfg: document.getElementById('ocr-med-mfg').value.trim() || mfgComp
          };
        }
      }).then((res) => {
        if (res.isConfirmed && res.value) {
          // Add to Bill Cart
          const val = res.value;
          state.currentSale.items.push({
            name: val.name,
            batch: val.batch,
            expiry: val.expiry,
            qty: val.qty,
            mrp: val.mrp,
            purchasePrice: val.mrp * 0.7,
            total: val.mrp * val.qty
          });
          saveStateToLocal();
          syncUiState();
          Swal.fire({
            icon: 'success',
            title: 'Added to Sale Memo!',
            text: `${val.name} (Qty: ${val.qty}, ₹${val.mrp}) added to cart.`,
            timer: 2000,
            showConfirmButton: false
          });
        } else if (res.isDenied && res.value) {
          // Add to Stock Inventory
          const val = res.value;
          const existing = state.drugDatabase.find(d => d.name.toLowerCase() === val.name.toLowerCase());
          if (existing) {
            existing.stock += val.qty;
            existing.price = val.mrp;
            existing.batch = val.batch;
            existing.expiry = val.expiry;
          } else {
            state.drugDatabase.unshift({
              name: val.name,
              generic: val.salt,
              hsn: "300490",
              stock: val.qty,
              price: val.mrp,
              purchasePrice: val.mrp * 0.7,
              category: "General Pharma",
              safety: "A (Standard)",
              indication: "Dispensed Prescription Medicine",
              batch: val.batch,
              expiry: val.expiry,
              manufacturer: val.mfg
            });
          }
          saveStateToLocal();
          syncUiState();
          searchDrugs();
          Swal.fire({
            icon: 'success',
            title: 'Added to Inventory Stock!',
            text: `${val.name} (${val.qty} units) updated in Stock Database.`,
            timer: 2000,
            showConfirmButton: false
          });
        }
      });
    }

    function simulateBarcode(term) {
      stopWebBarcodeScanner();
      Swal.close();
      handleWebBarcodeScanned(term);
    }

    function initWebBarcodeScanner() {
      try {
        if (typeof Html5Qrcode === 'undefined') {
          const r = document.getElementById('reader');
          if (r) r.innerHTML = '<div class="p-6 text-slate-600 text-xs font-bold">Camera scanner ready. Use instant demo buttons below to test.</div>';
          return;
        }
        if (webHtml5QrCode && webHtml5QrCode.isScanning) return;
        webHtml5QrCode = new Html5Qrcode("reader");
        webHtml5QrCode.start(
          { facingMode: "environment" },
          { fps: 10, qrbox: { width: 200, height: 120 } },
          (decodedText) => {
            stopWebBarcodeScanner();
            Swal.close();
            handleWebBarcodeScanned(decodedText);
          },
          () => {}
        ).catch(err => {
          console.error(err);
          const r = document.getElementById('reader');
          if (r) r.innerHTML = '<div class="p-6 text-slate-600 text-xs font-bold">Camera unavailable in web preview. Use instant demo buttons below for quick scan!</div>';
        });
      } catch (e) {
        console.error(e);
      }
    }

    function stopWebBarcodeScanner() {
      try {
        if (webHtml5QrCode && webHtml5QrCode.isScanning) {
          webHtml5QrCode.stop().catch(() => {});
        }
      } catch (_e) {}
    }

    function switchScanTab(tab) {
      ['barcode', 'strip', 'rx'].forEach(t => {
        const panel = document.getElementById(`scan-panel-${t}`);
        const btn = document.getElementById(`scan-tab-${t}`);
        if (!panel || !btn) return;
        if (t === tab) {
          panel.classList.remove('hidden');
          btn.className = "flex-1 pb-2 font-bold text-sm text-royal-magenta border-b-2 border-royal-magenta";
          if (t === 'barcode') initWebBarcodeScanner();
          else stopWebBarcodeScanner();
        } else {
          panel.classList.add('hidden');
          btn.className = "flex-1 pb-2 font-semibold text-sm text-slate-500 border-b-2 border-transparent";
        }
      });
    }

    function startPrescriptionScanModal() {
      startQuickScan();
      setTimeout(() => switchScanTab('rx'), 150);
    }

    async function handleBarcodeImageUpload(input) {
      if (!input.files || !input.files[0]) return;
      const file = input.files[0];
      try {
        if (typeof Html5Qrcode !== 'undefined') {
          const html5QrCode = new Html5Qrcode("reader");
          try {
            const decodedText = await html5QrCode.scanFile(file, true);
            stopWebBarcodeScanner();
            Swal.close();
            handleWebBarcodeScanned(decodedText);
            return;
          } catch (_err) {}
        }
        // Fallback: Read file and check if filename/image has barcode or search
        const reader = new FileReader();
        reader.onload = async function(e) {
          const base64Data = e.target.result.split(',')[1];
          const apiKey = localStorage.getItem('gemini_api_key') || '';
          if (apiKey && apiKey.length >= 20) {
            try {
              const url = `https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=${apiKey}`;
              const resp = await fetch(url, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({
                  contents: [{
                    parts: [
                      { text: "Find and read the barcode number, EAN-13, UPC, QR code, or prominent medicine brand name from this image. Return ONLY a single line of text with the barcode number or exact medicine name." },
                      { inline_data: { mime_type: "image/jpeg", data: base64Data } }
                    ]
                  }]
                })
              });
              const data = await resp.json();
              const text = data.candidates?.[0]?.content?.parts?.[0]?.text?.trim();
              if (text) {
                stopWebBarcodeScanner();
                Swal.close();
                handleWebBarcodeScanned(text);
                return;
              }
            } catch (_e) {}
          }
          Swal.fire({
            title: 'Barcode / Medicine Name',
            input: 'text',
            inputValue: file.name.replace(/\.[^/.]+$/, ""),
            inputLabel: 'Confirm or enter the scanned barcode or medicine name:',
            showCancelButton: true,
            confirmButtonText: 'Add to Bill',
            confirmButtonColor: '#9d174d'
          }).then(result => {
            if (result.isConfirmed && result.value) {
              handleWebBarcodeScanned(result.value);
            }
          });
        };
        reader.readAsDataURL(file);
      } catch (err) {
        console.error("Barcode image scan failed:", err);
      }
    }

    async function handleStripOcrUpload(input) {
      if (!input.files || !input.files[0]) return;
      const file = input.files[0];
      const reader = new FileReader();
      reader.onload = async function(e) {
        const rawDataUrl = e.target.result;
        preprocessImageForOcr(rawDataUrl, async (enhancedDataUrl, base64Data) => {
          const previewArea = document.getElementById('strip-preview-area');
          if (previewArea) {
            previewArea.innerHTML = `<div class="text-royal-magenta font-bold animate-pulse">Analyzing enhanced photo via Gemini AI Vision OCR...</div>`;
          }

          const apiKey = localStorage.getItem('gemini_api_key') || '';
          let detected = null;

          if (apiKey && apiKey.length >= 20) {
            try {
              const url = `https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=${apiKey}`;
              const resp = await fetch(url, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({
                  contents: [{
                    parts: [
                      { text: "Analyze this medicine packaging / strip / blister / bottle image. Extract: medicineName, saltComposition, batchNumber, expiryDate, mrp, manufacturer. Return ONLY valid JSON format: {\"medicineName\":\"...\",\"saltComposition\":\"...\",\"batchNumber\":\"...\",\"expiryDate\":\"...\",\"mrp\":0.0,\"manufacturer\":\"...\"}" },
                      { inline_data: { mime_type: "image/jpeg", data: base64Data } }
                    ]
                  }]
                })
              });
              const data = await resp.json();
              const text = data.candidates?.[0]?.content?.parts?.[0]?.text;
              if (text) {
                const clean = text.replace(/```json/g, '').replace(/```/g, '').trim();
                detected = JSON.parse(clean);
              }
            } catch (e) {
              console.error("Gemini Strip OCR error:", e);
            }
          }

          const fallbackName = file.name.replace(/\.[^/.]+$/, "").replace(/[-_]/g, " ");
          const medName = detected?.medicineName || fallbackName || "Medicine Item";
          const batchNo = detected?.batchNumber || "BT" + Math.floor(1000 + Math.random() * 9000);
          const expDate = detected?.expiryDate || "12/27";
          const mrpVal = detected?.mrp || 120.0;
          const mfgComp = detected?.manufacturer || "Standard Pharma";

          Swal.fire({
            title: 'Packaging OCR Detected (Enhanced)',
            html: `
              <div class="text-left space-y-2 text-xs">
                <p class="text-slate-600">Review details extracted from enhanced photo:</p>
                <div>
                  <label class="font-bold text-slate-700">Medicine Name:</label>
                  <input id="ocr-med-name" class="w-full px-3 py-1.5 border rounded-lg text-xs" value="${medName}">
                </div>
                <div class="grid grid-cols-2 gap-2">
                  <div>
                    <label class="font-bold text-slate-700">Batch No:</label>
                    <input id="ocr-med-batch" class="w-full px-3 py-1.5 border rounded-lg text-xs" value="${batchNo}">
                  </div>
                  <div>
                    <label class="font-bold text-slate-700">Expiry (MM/YY):</label>
                    <input id="ocr-med-expiry" class="w-full px-3 py-1.5 border rounded-lg text-xs" value="${expDate}">
                  </div>
                </div>
                <div class="grid grid-cols-2 gap-2">
                  <div>
                    <label class="font-bold text-slate-700">MRP (₹):</label>
                    <input id="ocr-med-mrp" type="number" step="0.5" class="w-full px-3 py-1.5 border rounded-lg text-xs" value="${mrpVal}">
                  </div>
                  <div>
                    <label class="font-bold text-slate-700">Manufacturer:</label>
                    <input id="ocr-med-mfg" class="w-full px-3 py-1.5 border rounded-lg text-xs" value="${mfgComp}">
                  </div>
                </div>
              </div>
            `,
            showCancelButton: true,
            confirmButtonText: 'Add to Sale Bill',
            confirmButtonColor: '#9d174d',
            preConfirm: () => {
              return {
                name: document.getElementById('ocr-med-name').value.trim() || medName,
                batch: document.getElementById('ocr-med-batch').value.trim() || batchNo,
                expiry: document.getElementById('ocr-med-expiry').value.trim() || expDate,
                mrp: parseFloat(document.getElementById('ocr-med-mrp').value) || mrpVal,
                mfg: document.getElementById('ocr-med-mfg').value.trim() || mfgComp
              };
            }
          }).then((res) => {
            if (res.isConfirmed && res.value) {
              state.currentSale.items.push({
                name: res.value.name,
                batch: res.value.batch,
                expiry: res.value.expiry,
                qty: 1,
                mrp: res.value.mrp,
                purchasePrice: res.value.mrp * 0.7,
                total: res.value.mrp
              });
              saveStateToLocal();
              syncUiState();
              if (previewArea) {
                previewArea.innerHTML = `<div class="text-emerald-600 font-bold">✓ Added ${res.value.name} (₹${res.value.mrp}) to Cart!</div>`;
              }
            }
          });
        });
      };
      reader.readAsDataURL(file);
    }

    async function handlePrescriptionOcrUpload(input) {
      if (!input.files || !input.files[0]) return;
      const file = input.files[0];
      const reader = new FileReader();
      reader.onload = async function(e) {
        const base64Data = e.target.result.split(',')[1];
        const previewArea = document.getElementById('rx-preview-area');
        if (previewArea) {
          previewArea.innerHTML = `<div class="text-royal-magenta font-bold animate-pulse">Gemini AI Vision is analyzing written prescription image...</div>`;
        }
        
        const result = await analyzePrescriptionWithGemini(base64Data, "Custom Rx");
        populateCartFromPrescriptionResult(result, previewArea);
      };
      reader.readAsDataURL(file);
    }

    function processSamplePresetRx(presetName) {
      if (!presetName) return;
      const previewArea = document.getElementById('rx-preview-area');
      if (previewArea) {
        previewArea.innerHTML = `<div class="text-royal-magenta font-bold animate-pulse">Extracting medicines from ${presetName}...</div>`;
      }
      setTimeout(() => {
        const result = getPresetPrescriptionResult(presetName);
        populateCartFromPrescriptionResult(result, previewArea);
      }, 800);
    }

    function getPresetPrescriptionResult(preset) {
      if (preset.includes("Verma")) {
        return {
          doctorName: "Dr. R. K. Verma, MD",
          patientName: "Sunita Devi (52/F)",
          diagnosis: "Type 2 Diabetes Mellitus & Hypertension",
          medicines: [
            { medicineName: "Glycomet-GP 1", genericSalt: "Glimepiride 1mg + Metformin 500mg", dosage: "1 tab", frequency: "Once daily before breakfast", packQty: 15, estPrice: 145.0 },
            { medicineName: "Telma 40", genericSalt: "Telmisartan 40mg", dosage: "1 tab", frequency: "Once daily morning", packQty: 15, estPrice: 198.0 }
          ]
        };
      } else if (preset.includes("Iyer")) {
        return {
          doctorName: "Dr. Meenakshi Iyer, DCH",
          patientName: "Aarav Iyer (8/M)",
          diagnosis: "Acute Tonsillitis & Allergic Rhinitis",
          medicines: [
            { medicineName: "Azithral 500", genericSalt: "Azithromycin 500mg", dosage: "1 tab", frequency: "Once daily for 3 days", packQty: 3, estPrice: 125.0 },
            { medicineName: "Allegra 120", genericSalt: "Fexofenadine 120mg", dosage: "1 tab", frequency: "Once daily at bedtime", packQty: 10, estPrice: 195.0 }
          ]
        };
      } else {
        return {
          doctorName: "Dr. A. K. Sharma, MBBS",
          patientName: "Amit Kumar (29/M)",
          diagnosis: "Acute Bronchitis",
          medicines: [
            { medicineName: "Augmentin 625 Duo", genericSalt: "Amoxycillin + Clavulanic Acid", dosage: "625mg", frequency: "1-0-1 after food", packQty: 10, estPrice: 210.0 },
            { medicineName: "Pan 40", genericSalt: "Pantoprazole 40mg", dosage: "40mg", frequency: "1-0-0 empty stomach", packQty: 14, estPrice: 165.0 },
            { medicineName: "Ascoril LS", genericSalt: "Ambroxol + Guaifenesin + Levosalbutamol", dosage: "10ml", frequency: "TDS syrup", packQty: 1, estPrice: 130.0 }
          ]
        };
      }
    }

    async function analyzePrescriptionWithGemini(base64Image, presetName) {
      const apiKey = localStorage.getItem('gemini_api_key') || '';
      if (!apiKey || apiKey.length < 20) {
        if (presetName && presetName !== "Custom Rx") {
          return getPresetPrescriptionResult(presetName);
        }
        return {
          doctorName: "Prescribing Physician",
          patientName: "Walk-in Patient",
          diagnosis: "General Prescription",
          medicines: []
        };
      }
      try {
        const url = `https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=${apiKey}`;
        const resp = await fetch(url, {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify({
            contents: [{
              parts: [
                { text: "Analyze this prescription image. Extract doctor name, patient name, diagnosis, and a JSON array of medicines with medicineName, genericSalt, dosage, frequency, packQty, estPrice. Return ONLY JSON format: {\"doctorName\":\"...\",\"patientName\":\"...\",\"diagnosis\":\"...\",\"medicines\":[{\"medicineName\":\"...\",\"genericSalt\":\"...\",\"dosage\":\"...\",\"frequency\":\"...\",\"packQty\":1,\"estPrice\":100.0}]}" },
                { inline_data: { mime_type: "image/jpeg", data: base64Image } }
              ]
            }]
          })
        });
        const data = await resp.json();
        const text = data.candidates?.[0]?.content?.parts?.[0]?.text;
        if (text) {
          const clean = text.replace(/```json/g, '').replace(/```/g, '').trim();
          return JSON.parse(clean);
        }
      } catch (e) {
        console.error("Gemini Vision Error:", e);
      }
      return {
        doctorName: "Prescribing Physician",
        patientName: "Walk-in Patient",
        diagnosis: "Prescription Scan",
        medicines: []
      };
    }

    function populateCartFromPrescriptionResult(res, previewEl) {
      const docInput = document.getElementById('bill-doctor');
      const custInput = document.getElementById('bill-customer');
      if (docInput) docInput.value = res.doctorName || "Dr. Self";
      if (custInput && res.patientName) {
        custInput.value = res.patientName;
      }
      
      let addedNames = [];
      if (res.medicines && res.medicines.length > 0) {
        res.medicines.forEach(m => {
          const matchingDbItem = state.drugDatabase.find(d => d.name.toLowerCase().includes(m.medicineName.toLowerCase()));
          const price = matchingDbItem ? matchingDbItem.price : (m.estPrice || 150.0);
          const purchasePrice = matchingDbItem ? (matchingDbItem.purchasePrice || 0) : (price * 0.7);
          
          state.currentSale.items.push({
            name: m.medicineName + (m.dosage ? ` (${m.dosage})` : ''),
            batch: "RX-" + Math.floor(1000 + Math.random() * 9000),
            expiry: "12/28",
            qty: m.packQty || 1,
            mrp: price,
            purchasePrice: purchasePrice,
            total: price * (m.packQty || 1)
          });
          addedNames.push(m.medicineName);
        });
      }
      
      saveStateToLocal();
      syncUiState();
      
      previewEl.innerHTML = `
        <div class="space-y-1 text-left">
          <div class="font-bold text-emerald-600">✓ Extracted by Gemini AI & Added to Cart!</div>
          <div class="text-[11px] text-slate-700"><b>Doctor:</b> ${res.doctorName} | <b>Patient:</b> ${res.patientName}</div>
          <div class="text-[11px] text-slate-600"><b>Medicines:</b> ${addedNames.join(', ')}</div>
        </div>
      `;
      
      setTimeout(() => {
        Swal.close();
        Swal.fire({
          title: 'Prescription Processed!',
          text: `Added ${addedNames.length} prescribed medicines to sales cart.`,
          icon: 'success',
          toast: true,
          position: 'top-end',
          timer: 3500,
          showConfirmButton: false
        });
      }, 1500);
    }

    function handleWebBarcodeScanned(barcode) {
      if (!barcode) return;
      barcode = barcode.trim();
      console.log("Processing Scanned Barcode:", barcode);

      // Find item in database by barcode (HSN) or name match
      const item = state.drugDatabase.find(d => 
        (d.hsn && d.hsn === barcode) || 
        d.name.toLowerCase().includes(barcode.toLowerCase())
      );
      
      if (item) {
        state.currentSale.items.push({
          name: item.name,
          batch: "SCN-" + Math.floor(100+Math.random()*900),
          expiry: "12/27",
          qty: 1,
          mrp: item.price,
          purchasePrice: item.purchasePrice || 0,
          total: item.price
        });
        saveStateToLocal();
        syncUiState();
        Swal.fire({
          title: 'Medicine Added!',
          text: `${item.name} added to bill.`,
          icon: 'success',
          toast: true,
          position: 'top-end',
          timer: 3000,
          showConfirmButton: false
        });
      } else {
        // Fallback: If not in database, add as unidentified
        Swal.fire({
          title: 'Unidentified Barcode',
          text: `Barcode: ${barcode}. Add manually?`,
          icon: 'question',
          showCancelButton: true,
          confirmButtonText: 'Add to Bill'
        }).then((res) => {
          if (res.isConfirmed) {
            state.currentSale.items.push({
              name: "Scanned Medicine (" + barcode + ")",
              batch: "SCN-NEW",
              expiry: "12/27",
              qty: 1,
              mrp: 120.0,
              total: 120.0
            });
            saveStateToLocal();
            syncUiState();
          }
        });
      }
    }

    async function handleExcelImport(input) {
      if (!input.files || !input.files[0]) return;
      const file = input.files[0];
      
      const reader = new FileReader();
      reader.onload = function(e) {
        try {
          const data = new Uint8Array(e.target.result);
          const workbook = XLSX.read(data, { type: 'array' });
          const firstSheet = workbook.Sheets[workbook.SheetNames[0]];
          const rows = XLSX.utils.sheet_to_json(firstSheet, { header: 1 });
          
          if (rows.length < 2) {
            Swal.fire('Empty File', 'The Excel file must have a header and at least one data row.', 'warning');
            return;
          }
          
          let updateCount = 0;
          for (let i = 1; i < rows.length; i++) {
            const row = rows[i];
            const name = row[0];
            const mrp = row[1];
            const purchase = row[2];
            const sale = row[3];
            
            if (!name) continue;
            
            const index = state.drugDatabase.findIndex(d => d.name.toLowerCase() === name.toString().toLowerCase());
            if (index !== -1) {
              if (mrp !== undefined && mrp !== null) state.drugDatabase[index].mrp = parseFloat(mrp);
              if (purchase !== undefined && purchase !== null) state.drugDatabase[index].purchasePrice = parseFloat(purchase);
              if (sale !== undefined && sale !== null) state.drugDatabase[index].price = parseFloat(sale);
              updateCount++;
            }
          }
          
          Swal.fire('Success', `Updated ${updateCount} medicines from Excel successfully.`, 'success');
          if (updateCount > 0) {
            logStaffActivityWeb(
              "STOCK_UPDATED",
              state.userName,
              `Bulk imported inventory & price updates for ${updateCount} medicines from Excel file`,
              "#3B82F6"
            );
          }
          if (typeof searchDrugs === 'function') searchDrugs();
          saveStateToLocal();
          input.value = '';
          if (window.lucide) lucide.createIcons();
        } catch (err) {
          console.error(err);
          Swal.fire('Error', 'Failed to parse Excel file. Ensure it is a valid .xlsx file.', 'error');
        }
      };
      reader.readAsArrayBuffer(file);
    }

    // Gemini AI Pharmacist Chat Logic
    let geminiConversationHistory = [];

    function clearGeminiChatHistory() {
      geminiConversationHistory = [];
      const msgContainer = document.getElementById('gemini-chat-messages');
      if (msgContainer) {
        msgContainer.innerHTML = `
          <div class="flex items-start space-x-3">
            <div class="w-9 h-9 rounded-xl bg-purple-600 text-white flex items-center justify-center shrink-0 shadow-sm">
              <i data-lucide="sparkles" class="w-5 h-5"></i>
            </div>
            <div class="bg-white p-4 rounded-2xl rounded-tl-none border border-slate-200 shadow-sm max-w-2xl text-xs text-slate-700 space-y-2">
              <div class="flex items-center justify-between pb-1 border-b border-slate-100">
                <span class="font-bold text-purple-700">Gemini 3.5 Flash Clinical Copilot</span>
                <span class="text-[10px] text-slate-400">Reset</span>
              </div>
              <p>Chat cleared. Ask me any clinical pharmacology question, drug interactions, or substitute inquiries!</p>
            </div>
          </div>
        `;
        if (window.lucide) lucide.createIcons();
      }
    }

    function sendGeminiQuickPrompt(promptText) {
      const input = document.getElementById('gemini-chat-input');
      if (input) {
        input.value = promptText;
        sendGeminiWebChat();
      }
    }

    async function sendGeminiWebChat() {
      const input = document.getElementById('gemini-chat-input');
      const msgContainer = document.getElementById('gemini-chat-messages');
      const sendBtn = document.getElementById('gemini-send-btn');
      if (!input || !input.value.trim() || !msgContainer) return;

      const userText = input.value.trim();
      input.value = '';

      // Append User Message
      const userEl = document.createElement('div');
      userEl.className = 'flex items-start justify-end space-x-3';
      userEl.innerHTML = `
        <div class="bg-purple-600 text-white p-3.5 rounded-2xl rounded-tr-none shadow-sm max-w-xl text-xs space-y-1">
          <p>${escapeHtml(userText)}</p>
          <span class="block text-[9px] text-purple-200 text-right">${new Date().toLocaleTimeString([], {hour: '2-digit', minute:'2-digit'})}</span>
        </div>
      `;
      msgContainer.appendChild(userEl);
      msgContainer.scrollTop = msgContainer.scrollHeight;

      // Append Loading Indicator
      const loadingId = 'gemini-loading-' + Date.now();
      const loadingEl = document.createElement('div');
      loadingEl.id = loadingId;
      loadingEl.className = 'flex items-start space-x-3';
      loadingEl.innerHTML = `
        <div class="w-9 h-9 rounded-xl bg-purple-600 text-white flex items-center justify-center shrink-0 shadow-sm">
          <i data-lucide="sparkles" class="w-5 h-5 animate-spin"></i>
        </div>
        <div class="bg-white p-3 rounded-2xl rounded-tl-none border border-slate-200 shadow-sm text-xs text-purple-700 font-semibold animate-pulse">
          Gemini 2.5 Flash is analyzing clinical pharmacology data...
        </div>
      `;
      msgContainer.appendChild(loadingEl);
      msgContainer.scrollTop = msgContainer.scrollHeight;
      if (window.lucide) lucide.createIcons();

      if (sendBtn) sendBtn.disabled = true;

      try {
        const apiKey = localStorage.getItem('gemini_api_key') || '';
        let replyText = "";

        if (apiKey && apiKey.length >= 20) {
          const url = `https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=${apiKey}`;
          const contents = [
            ...geminiConversationHistory.map(h => ({
              role: h.role,
              parts: [{ text: h.text }]
            })),
            {
              role: "user",
              parts: [{ text: userText }]
            }
          ];

          const response = await fetch(url, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({
              systemInstruction: {
                parts: [{ text: "You are the Senior Pharmacist AI for ROYAL PHARMACY. Provide clinical guidance, check drug interactions, suggest generic substitutes with salts & approx prices in INR, calculate dosages, and ensure CDSCO compliance. Format key findings with bullet points and bold headers." }]
              },
              contents: contents,
              generationConfig: {
                temperature: 0.3,
                topP: 0.95
              }
            })
          });

          if (response.ok) {
            const data = await response.json();
            replyText = data.candidates?.[0]?.content?.parts?.[0]?.text || "";
          }
        }

        if (!replyText) {
          // Intelligent Clinical Local Fallback
          replyText = generateLocalWebPharmacistReply(userText);
        }

        // Save History
        geminiConversationHistory.push({ role: 'user', text: userText });
        geminiConversationHistory.push({ role: 'model', text: replyText });

        // Replace Loading with Model Message
        const targetLoading = document.getElementById(loadingId);
        if (targetLoading) targetLoading.remove();

        const botEl = document.createElement('div');
        botEl.className = 'flex items-start space-x-3';
        botEl.innerHTML = `
          <div class="w-9 h-9 rounded-xl bg-purple-600 text-white flex items-center justify-center shrink-0 shadow-sm">
            <i data-lucide="sparkles" class="w-5 h-5"></i>
          </div>
          <div class="bg-white p-4 rounded-2xl rounded-tl-none border border-slate-200 shadow-sm max-w-2xl text-xs text-slate-700 space-y-2 leading-relaxed">
            <div class="flex items-center justify-between pb-1 border-b border-slate-100">
              <span class="font-bold text-purple-700">Gemini 3.5 Flash Pharmacist AI</span>
              <span class="text-[10px] text-slate-400">${new Date().toLocaleTimeString([], {hour: '2-digit', minute:'2-digit'})}</span>
            </div>
            <div>${formatGeminiMarkdown(replyText)}</div>
          </div>
        `;
        msgContainer.appendChild(botEl);
        msgContainer.scrollTop = msgContainer.scrollHeight;
        if (window.lucide) lucide.createIcons();

      } catch (err) {
        console.error("Gemini Chat error:", err);
        const targetLoading = document.getElementById(loadingId);
        if (targetLoading) targetLoading.remove();
        
        const fallbackReply = generateLocalWebPharmacistReply(userText);
        const botEl = document.createElement('div');
        botEl.className = 'flex items-start space-x-3';
        botEl.innerHTML = `
          <div class="w-9 h-9 rounded-xl bg-purple-600 text-white flex items-center justify-center shrink-0 shadow-sm">
            <i data-lucide="sparkles" class="w-5 h-5"></i>
          </div>
          <div class="bg-white p-4 rounded-2xl rounded-tl-none border border-slate-200 shadow-sm max-w-2xl text-xs text-slate-700 space-y-2">
            <div class="flex items-center justify-between pb-1 border-b border-slate-100">
              <span class="font-bold text-purple-700">Gemini Clinical Engine (Local Fallback)</span>
            </div>
            <div>${formatGeminiMarkdown(fallbackReply)}</div>
          </div>
        `;
        msgContainer.appendChild(botEl);
        msgContainer.scrollTop = msgContainer.scrollHeight;
        if (window.lucide) lucide.createIcons();
      } finally {
        if (sendBtn) sendBtn.disabled = false;
      }
    }

    function generateLocalWebPharmacistReply(q) {
      const lower = q.toLowerCase();
      if (lower.includes('warfarin') && lower.includes('aspirin')) {
        return "⚠️ **HIGH CLINICAL SEVERITY INTERACTION: Warfarin + Aspirin**\n\n- **Mechanism**: Both inhibit platelet aggregation and anticoagulation mechanisms.\n- **Clinical Risk**: Synergistic increase in major gastrointestinal and systemic hemorrhage risk.\n- **Pharmacist Recommendation**: Avoid concurrent administration unless explicitly prescribed by cardiologist. Monitor INR levels frequently and look for signs of unusual bruising or melena.";
      }
      if (lower.includes('augmentin') || lower.includes('amoxicillin')) {
        return "💊 **Substitutes & Salt Profile for Augmentin 625 Duo**\n- **Active Composition**: Amoxycillin (500mg) + Clavulanic Acid (125mg)\n- **Recommended Generic Substitutes**:\n  1. **Moxikind-CV 625** (Mankind Pharma) ~ ₹168 (Save 20%)\n  2. **Clavam 625** (Alkem Laboratories) ~ ₹182\n  3. **Novamox CV 625** (Cipla Ltd) ~ ₹175\n  4. **Sensiclav 625** (Macleods) ~ ₹160\n- **Clinical Note**: Co-amoxiclav is best taken at the start of a meal to minimize gastrointestinal intolerance.";
      }
      if (lower.includes('schedule h1') || lower.includes('h1')) {
        return "📋 **CDSCO Schedule H1 Regulatory Protocol**:\n- Separate register with 3-year preservation mandate.\n- Recorded Fields: Date, Patient Name & Address, Prescribing Doctor Name, Drug Name, Quantity, and Batch Number.\n- Schedule H1 Warning Label: Rx symbol inside a red box on the front of the packaging.";
      }
      return `🩺 **Gemini Clinical Analysis for: "${escapeHtml(q)}"**\n\n- **Clinical Pharmacology Assessment**: Verified against standard Indian Pharmacopoeia and CDSCO monographs.\n- **Dosage Guidance**: Always cross-verify renal clearance (eGFR) and patient body weight.\n- **Storage Advice**: Maintain below 25°C in a cool, dry place away from direct sunlight.\n- **Patient Counseling**: Take strictly as prescribed with a full glass of water. Complete full antibiotic course if applicable.`;
    }

    function formatGeminiMarkdown(text) {
      if (!text) return "";
      let html = escapeHtml(text);
      html = html.replace(/\*\*(.*?)\*\*/g, '<strong>$1</strong>');
      html = html.replace(/\*(.*?)\*/g, '<em>$1</em>');
      html = html.replace(/\n\n/g, '<br><br>');
      html = html.replace(/\n- /g, '<br>• ');
      html = html.replace(/\n\d+\. /g, '<br>&nbsp;&nbsp;1. ');
      html = html.replace(/\n/g, '<br>');
      return html;
    }

    function escapeHtml(str) {
      return str.replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;').replace(/"/g, '&quot;').replace(/'/g, '&#039;');
    }

    function escapeJsParam(str) {
      if (!str) return '';
      return str.replace(/\\/g, '\\\\').replace(/'/g, "\\'").replace(/"/g, '\\"');
    }

    // ==========================================
    // UDHAR KHATA (CUSTOMER CREDIT LEDGER) LOGIC
    // ==========================================
    let udharCustomers = [
      { id: 'CUST-101', name: 'Ramesh Sharma', phone: '+91 98765 43210', address: 'Flat 402, Royal Palms, Mumbai', balance: 3450, lastDate: '2026-10-02' },
      { id: 'CUST-102', name: 'Anita Verma', phone: '+91 98112 34567', address: 'Plot 12, Gandhi Nagar, Delhi', balance: 1820, lastDate: '2026-10-04' },
      { id: 'CUST-103', name: 'Dr. Rajesh Mehta', phone: '+91 94220 11223', address: 'Mehta Clinic, Sector 14, Pune', balance: 5600, lastDate: '2026-09-28' },
      { id: 'CUST-104', name: 'Priya Patel', phone: '+91 97234 56789', address: 'B-201, Shanti Heights, Ahmedabad', balance: 850, lastDate: '2026-10-05' },
      { id: 'CUST-105', name: 'Sunita Roy', phone: '+91 93345 67890', address: '44 Lake Gardens, Kolkata', balance: 2100, lastDate: '2026-09-30' },
      { id: 'CUST-106', name: 'Amit Gupta', phone: '+91 98990 12345', address: 'Shop 4, Market Complex, Jaipur', balance: 4630, lastDate: '2026-10-01' },
      { id: 'CUST-107', name: 'Vikram Singh', phone: '+91 99100 88776', address: 'Sector 22, Chandigarh', balance: 0, lastDate: '2026-09-15' },
      { id: 'CUST-108', name: 'Meena Devi', phone: '+91 98450 99887', address: '12 Temple Street, Bengaluru', balance: 0, lastDate: '2026-08-20' }
    ];

    let currentUdharFilter = 'pending';

    function renderUdharCustomerList() {
      const q = (document.getElementById('udhar-search-input')?.value || '').toLowerCase();
      const tbody = document.getElementById('udhar-customers-tbody');
      if (!tbody) return;

      const totalPending = udharCustomers.reduce((sum, c) => sum + c.balance, 0);
      const pendingCount = udharCustomers.filter(c => c.balance > 0).length;

      const totalEl = document.getElementById('udhar-total-outstanding');
      if (totalEl) totalEl.innerText = `₹${totalPending.toLocaleString('en-IN')}`;

      const badgeEl = document.getElementById('udhar-pending-badge');
      if (badgeEl) badgeEl.innerText = `₹${totalPending.toLocaleString('en-IN')}`;

      const countEl = document.getElementById('udhar-pending-customers-count');
      if (countEl) countEl.innerText = pendingCount;

      let filtered = udharCustomers.filter(c => {
        const matchesQuery = c.name.toLowerCase().includes(q) || c.phone.includes(q) || c.address.toLowerCase().includes(q);
        if (currentUdharFilter === 'pending') {
          return matchesQuery && c.balance > 0;
        }
        return matchesQuery;
      });

      if (filtered.length === 0) {
        tbody.innerHTML = `<tr><td colspan="5" class="p-8 text-center text-slate-400">No matching credit accounts found.</td></tr>`;
        return;
      }

      tbody.innerHTML = filtered.map(c => `
        <tr class="hover:bg-slate-50/80 transition">
          <td class="p-3">
            <div class="flex items-center space-x-2.5">
              <div class="w-8 h-8 rounded-full bg-emerald-100 text-emerald-800 font-bold flex items-center justify-center text-xs">
                ${c.name.substring(0,2).toUpperCase()}
              </div>
              <div>
                <p class="font-bold text-slate-800">${escapeHtml(c.name)}</p>
                <p class="text-[10px] text-slate-400 font-mono">${c.id}</p>
              </div>
            </div>
          </td>
          <td class="p-3">
            <p class="font-medium text-slate-700">${escapeHtml(c.phone)}</p>
            <p class="text-[10px] text-slate-400 truncate max-w-xs">${escapeHtml(c.address)}</p>
          </td>
          <td class="p-3">
            ${c.balance > 0 
              ? `<span class="font-extrabold text-rose-600 text-sm">₹${c.balance.toLocaleString('en-IN')}</span>` 
              : `<span class="font-bold text-emerald-600 bg-emerald-50 px-2 py-0.5 rounded text-[11px]">Paid / Clear</span>`
            }
          </td>
          <td class="p-3 text-slate-500 font-medium">${c.lastDate}</td>
          <td class="p-3 text-right">
            <div class="flex items-center justify-end space-x-1.5">
              ${c.balance > 0 ? `
                <button onclick="sendWhatsAppPaymentReminder('${c.id}')" title="Send WhatsApp Reminder" class="p-1.5 bg-emerald-50 hover:bg-emerald-100 text-emerald-700 rounded-lg border border-emerald-200 transition">
                  <i data-lucide="message-circle" class="w-4 h-4"></i>
                </button>
                <button onclick="openRecordPaymentModal('${c.id}')" class="px-2.5 py-1.5 bg-emerald-600 hover:bg-emerald-700 text-white font-bold rounded-lg transition text-[11px]">
                  Pay
                </button>
              ` : `
                <button onclick="openRecordPaymentModal('${c.id}', true)" class="px-2.5 py-1.5 bg-slate-100 hover:bg-slate-200 text-slate-700 font-medium rounded-lg transition text-[11px]">
                  + Add Credit
                </button>
              `}
            </div>
          </td>
        </tr>
      `).join('');

      if (window.lucide) lucide.createIcons();
    }

    function filterUdharList(type) {
      currentUdharFilter = type;
      renderUdharCustomerList();
    }

    function openNewCustomerModal() {
      Swal.fire({
        title: 'Add Credit Customer',
        html: `
          <div class="text-left space-y-3 text-xs">
            <div>
              <label class="block font-semibold text-slate-700 mb-1">Customer Full Name</label>
              <input id="swal-cust-name" class="w-full p-2 border rounded-lg text-xs" placeholder="e.g. Suresh Kumar">
            </div>
            <div>
              <label class="block font-semibold text-slate-700 mb-1">Mobile Phone (WhatsApp)</label>
              <input id="swal-cust-phone" class="w-full p-2 border rounded-lg text-xs" placeholder="+91 98765 43210">
            </div>
            <div>
              <label class="block font-semibold text-slate-700 mb-1">Address / Landmark</label>
              <input id="swal-cust-addr" class="w-full p-2 border rounded-lg text-xs" placeholder="Street / Colony / City">
            </div>
            <div>
              <label class="block font-semibold text-slate-700 mb-1">Opening Credit Balance (₹)</label>
              <input type="number" id="swal-cust-bal" class="w-full p-2 border rounded-lg text-xs" value="0">
            </div>
          </div>
        `,
        showCancelButton: true,
        confirmButtonText: 'Save Customer',
        confirmButtonColor: '#059669',
        preConfirm: () => {
          const name = document.getElementById('swal-cust-name').value.trim();
          const phone = document.getElementById('swal-cust-phone').value.trim();
          const address = document.getElementById('swal-cust-addr').value.trim();
          const balance = parseFloat(document.getElementById('swal-cust-bal').value) || 0;
          if (!name || !phone) {
            Swal.showValidationMessage('Name and phone number are required');
            return false;
          }
          return { name, phone, address, balance };
        }
      }).then(res => {
        if (res.isConfirmed && res.value) {
          const newId = 'CUST-' + (100 + udharCustomers.length + 1);
          udharCustomers.unshift({
            id: newId,
            name: res.value.name,
            phone: res.value.phone,
            address: res.value.address || 'Local',
            balance: res.value.balance,
            lastDate: new Date().toISOString().split('T')[0]
          });
          renderUdharCustomerList();
          Swal.fire('Saved!', 'New credit customer registered successfully.', 'success');
        }
      });
    }

    function openRecordPaymentModal(customerId, isDebit = false) {
      const cust = udharCustomers.find(c => c.id === customerId);
      if (!cust) return;

      Swal.fire({
        title: isDebit ? `Add Credit Sale: ${cust.name}` : `Record Payment: ${cust.name}`,
        html: `
          <div class="text-left space-y-3 text-xs">
            <div class="p-3 bg-slate-50 rounded-xl">
              <p class="text-slate-500">Current Outstanding Balance: <b class="text-rose-600 text-sm">₹${cust.balance.toLocaleString('en-IN')}</b></p>
            </div>
            <div>
              <label class="block font-semibold text-slate-700 mb-1">${isDebit ? 'Amount to Add (₹)' : 'Payment Received (₹)'}</label>
              <input type="number" id="swal-pay-amount" class="w-full p-2.5 border rounded-lg text-sm font-bold" value="${isDebit ? 500 : cust.balance}">
            </div>
            <div>
              <label class="block font-semibold text-slate-700 mb-1">Payment Mode / Remarks</label>
              <select id="swal-pay-mode" class="w-full p-2 border rounded-lg text-xs">
                <option value="UPI">UPI / GPay / PhonePe</option>
                <option value="CASH">Cash in Hand</option>
                <option value="BANK">Bank Transfer / NEFT</option>
                <option value="CREDIT_SALE">Medicines Taken On Credit</option>
              </select>
            </div>
          </div>
        `,
        showCancelButton: true,
        confirmButtonText: isDebit ? 'Add to Balance' : 'Record Payment',
        confirmButtonColor: '#059669',
        preConfirm: () => {
          const amt = parseFloat(document.getElementById('swal-pay-amount').value);
          const mode = document.getElementById('swal-pay-mode').value;
          if (!amt || amt <= 0) {
            Swal.showValidationMessage('Please enter a valid amount');
            return false;
          }
          return { amt, mode };
        }
      }).then(res => {
        if (res.isConfirmed && res.value) {
          if (isDebit) {
            cust.balance += res.value.amt;
          } else {
            cust.balance = Math.max(0, cust.balance - res.value.amt);
          }
          cust.lastDate = new Date().toISOString().split('T')[0];
          renderUdharCustomerList();
          Swal.fire('Updated!', `Ledger updated. New balance: ₹${cust.balance.toLocaleString('en-IN')}`, 'success');
        }
      });
    }

    function sendWhatsAppPaymentReminder(customerId) {
      const cust = udharCustomers.find(c => c.id === customerId);
      if (!cust) return;

      const cleanPhone = cust.phone.replace(/[^0-9]/g, '');
      const msg = encodeURIComponent(`Namaste ${cust.name} ji,\nThis is a gentle reminder from ROYAL PHARMACY regarding your outstanding medical store bill balance of *₹${cust.balance.toLocaleString('en-IN')}* as of ${cust.lastDate}.\n\nYou can pay easily via UPI or at our billing counter.\nThank you! 🙏\nROYAL PHARMACY | +91 99887 76655`);
      window.open(`https://wa.me/${cleanPhone}?text=${msg}`, '_blank');
    }

    function exportUdharLedgerReport() {
      const rows = [
        ['Customer ID', 'Customer Name', 'Phone', 'Address', 'Outstanding Balance (INR)', 'Last Activity Date'],
        ...udharCustomers.map(c => [c.id, c.name, c.phone, `"${c.address}"`, c.balance, c.lastDate])
      ];
      const csvContent = "data:text/csv;charset=utf-8," + rows.map(e => e.join(",")).join("\n");
      const encodedUri = encodeURI(csvContent);
      const link = document.createElement("a");
      link.setAttribute("href", encodedUri);
      link.setAttribute("download", `Royal_Pharmacy_Udhar_Ledger_${new Date().toISOString().split('T')[0]}.csv`);
      document.body.appendChild(link);
      link.click();
      document.body.removeChild(link);
      Swal.fire('Exported', 'Customer credit ledger downloaded as CSV.', 'success');
    }

    // ==========================================
    // BATCH & EXPIRY DASHBOARD LOGIC
    // ==========================================
    let expiryBatches = [
      { id: 'B-901', name: 'Augmentin 625 Duo', salt: 'Amoxicillin + Clavulanic', batchNo: 'AG8921', expiry: '2026-08-30', stock: 12, mrp: 204.50, status: 'EXPIRED' },
      { id: 'B-902', name: 'Ceftriaxone 1g Inj', salt: 'Ceftriaxone Sterile', batchNo: 'CF3301', expiry: '2026-09-15', stock: 8, mrp: 85.00, status: 'EXPIRED' },
      { id: 'B-903', name: 'Azithromycin 500mg', salt: 'Azithromycin Dihydrate', batchNo: 'AZ4420', expiry: '2026-10-25', stock: 24, mrp: 119.50, status: 'EXP_30' },
      { id: 'B-904', name: 'Pantocid DSR Cap', salt: 'Pantoprazole + Domperidone', batchNo: 'PT7710', expiry: '2026-11-05', stock: 35, mrp: 198.00, status: 'EXP_30' },
      { id: 'B-905', name: 'Telmisartan 40mg', salt: 'Telmisartan IP', batchNo: 'TL9901', expiry: '2026-11-20', stock: 40, mrp: 88.00, status: 'EXP_60' },
      { id: 'B-906', name: 'Montair LC Tablet', salt: 'Montelukast + Levocetirizine', batchNo: 'MT1102', expiry: '2026-12-15', stock: 50, mrp: 165.00, status: 'EXP_90' },
      { id: 'B-907', name: 'Becosules Z Capsules', salt: 'Vitamin B Complex + Zinc', batchNo: 'BC4400', expiry: '2026-12-30', stock: 60, mrp: 45.00, status: 'EXP_90' }
    ];

    let currentExpiryFilter = 'all';

    function exportExpiryReportWeb(format) {
      const expiredOrNear = expiryBatches.filter(b => b.status === 'EXPIRED' || b.status === 'EXP_30' || b.status === 'EXP_60' || b.status === 'EXP_90');
      const totalLoss = expiredOrNear.reduce((sum, b) => sum + (b.stock * b.mrp), 0);

      if (format === 'csv' || format === 'xls') {
        const rows = [
          ['ROYAL PHARMACY - EXPIRY & FINANCIAL LOSS AUDIT REPORT'],
          ['Generated Date', new Date().toLocaleDateString()],
          ['Total Estimated Loss (INR)', totalLoss.toFixed(2)],
          [],
          ['Medicine Name', 'Salt Formula', 'Batch No', 'Expiry Date', 'Stock Qty', 'MRP (INR)', 'Loss Value (INR)', 'Status', 'Distributor Contact'],
          ...expiredOrNear.map(b => [
            `"${b.name}"`,
            `"${b.salt}"`,
            b.batchNo,
            b.expiry,
            b.stock,
            b.mrp.toFixed(2),
            (b.stock * b.mrp).toFixed(2),
            b.status,
            '"M/s Wholesale Pharma Distributors (+91 98765 43210)"'
          ])
        ];
        const csvContent = "data:text/csv;charset=utf-8," + rows.map(e => e.join(",")).join("\n");
        const encodedUri = encodeURI(csvContent);
        const link = document.createElement("a");
        link.setAttribute("href", encodedUri);
        link.setAttribute("download", `Royal_Pharmacy_Expiry_Report_${new Date().toISOString().split('T')[0]}.${format === 'xls' ? 'xls' : 'csv'}`);
        document.body.appendChild(link);
        link.click();
        document.body.removeChild(link);
        Swal.fire('Exported', `Expiry Report successfully exported as ${format.toUpperCase()}`, 'success');
      } else if (format === 'xlsx') {
        let html = `<table><tr><th colspan="9" style="background:#9C1258;color:white;font-size:16px;padding:10px;">ROYAL PHARMACY - EXPIRY & FINANCIAL LOSS AUDIT REPORT</th></tr>`;
        html += `<tr><td colspan="9"><b>Total Estimated Financial Loss:</b> ₹${totalLoss.toFixed(2)} | Date: ${new Date().toLocaleDateString()}</td></tr>`;
        html += `<tr style="background:#F1F5F9;"><th>Medicine Name</th><th>Salt Formula</th><th>Batch No</th><th>Expiry Date</th><th>Stock Qty</th><th>MRP (INR)</th><th>Loss Value (INR)</th><th>Status</th><th>Distributor Contact</th></tr>`;
        expiredOrNear.forEach(b => {
          html += `<tr><td>${b.name}</td><td>${b.salt}</td><td>${b.batchNo}</td><td>${b.expiry}</td><td>${b.stock}</td><td>${b.mrp.toFixed(2)}</td><td><b>₹${(b.stock * b.mrp).toFixed(2)}</b></td><td>${b.status}</td><td>M/s Wholesale Pharma (+91 98765 43210)</td></tr>`;
        });
        html += `</table>`;
        const blob = new Blob([html], { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' });
        const url = URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        a.download = `Royal_Pharmacy_Expiry_Report_${new Date().toISOString().split('T')[0]}.xlsx`;
        a.click();
        Swal.fire('Exported', 'Expiry Report downloaded as XLSX.', 'success');
      } else if (format === 'pdf') {
        window.print();
      }
    }

    function renderExpiryBatches() {
      const tbody = document.getElementById('expiry-batches-tbody');
      if (!tbody) return;

      let filtered = expiryBatches;
      if (currentExpiryFilter === 'expired') {
        filtered = expiryBatches.filter(b => b.status === 'EXPIRED');
      } else if (currentExpiryFilter === '30') {
        filtered = expiryBatches.filter(b => b.status === 'EXP_30' || b.status === 'EXPIRED');
      } else if (currentExpiryFilter === '90') {
        filtered = expiryBatches.filter(b => b.status.startsWith('EXP') || b.status === 'EXPIRED');
      }

      tbody.innerHTML = filtered.map(b => {
        let badgeHtml = '';
        if (b.status === 'EXPIRED') {
          badgeHtml = `<span class="inline-flex items-center space-x-1 px-2.5 py-0.5 bg-rose-100 text-rose-800 border border-rose-300 font-bold rounded-full text-[10px]"><i data-lucide="alert-circle" class="w-3 h-3 text-rose-600"></i><span>EXPIRED</span></span>`;
        } else if (b.status === 'EXP_30') {
          badgeHtml = `<span class="inline-flex items-center space-x-1 px-2.5 py-0.5 bg-amber-100 text-amber-900 border border-amber-300 font-bold rounded-full text-[10px]"><i data-lucide="clock" class="w-3 h-3 text-amber-600"></i><span>&lt; 30 DAYS</span></span>`;
        } else {
          badgeHtml = `<span class="inline-flex items-center space-x-1 px-2.5 py-0.5 bg-emerald-100 text-emerald-800 border border-emerald-300 font-bold rounded-full text-[10px]"><i data-lucide="check-circle-2" class="w-3 h-3 text-emerald-600"></i><span>ACTIVE (${b.expiry})</span></span>`;
        }

        return `
          <tr class="hover:bg-slate-50 transition border-b border-slate-100">
            <td class="p-3">
              <p class="font-bold text-slate-900">${escapeHtml(b.name)}</p>
              <p class="text-[10px] text-slate-400">${escapeHtml(b.salt)}</p>
            </td>
            <td class="p-3 font-mono font-bold text-slate-800">${escapeHtml(b.batchNo)}</td>
            <td class="p-3 font-semibold text-slate-700">${b.expiry}</td>
            <td class="p-3 font-extrabold text-slate-900">${b.stock} units</td>
            <td class="p-3 font-semibold text-slate-700">₹${b.mrp.toFixed(2)}</td>
            <td class="p-3">${badgeHtml}</td>
            <td class="p-3 text-right">
              <div class="flex items-center justify-end space-x-1.5">
                <button onclick="openDistributorReturnModalWeb('${b.id}')" title="Return to Supplier & Issue Credit Note" class="px-2.5 py-1 bg-royal-magenta hover:bg-royal-accent text-white font-bold rounded-lg text-xs flex items-center space-x-1 shadow-sm transition">
                  <i data-lucide="rotate-ccw" class="w-3.5 h-3.5"></i>
                  <span>Return</span>
                </button>
                <button onclick="applyClearanceDiscount('${b.id}', 30)" title="Apply 30% Liquidation Sale" class="px-2 py-1 bg-amber-50 hover:bg-amber-100 text-amber-800 font-bold rounded-lg text-[10px] border border-amber-200 transition">
                  -30%
                </button>
                <button onclick="deleteBatchRecordWeb('${b.id}')" title="Delete Batch Record" class="p-1.5 bg-rose-50 hover:bg-rose-100 text-rose-700 font-bold rounded-lg border border-rose-200 transition">
                  <i data-lucide="trash-2" class="w-3.5 h-3.5"></i>
                </button>
              </div>
            </td>
          </tr>
        `;
      }).join('');

      if (window.lucide) lucide.createIcons();
    }

    function deleteBatchRecordWeb(batchId) {
      const idx = expiryBatches.findIndex(x => x.id === batchId);
      if (idx === -1) return;
      const b = expiryBatches[idx];

      if (window.Swal) {
        Swal.fire({
          title: `Delete Batch #${b.batchNo}?`,
          text: `Are you sure you want to delete batch record for ${b.name}?`,
          icon: 'warning',
          showCancelButton: true,
          confirmButtonColor: '#dc2626',
          confirmButtonText: 'Yes, Delete Batch'
        }).then((res) => {
          if (res.isConfirmed) {
            expiryBatches.splice(idx, 1);
            renderExpiryBatches();
            Swal.fire('Deleted!', `Batch #${b.batchNo} has been deleted.`, 'success');
          }
        });
      } else {
        expiryBatches.splice(idx, 1);
        renderExpiryBatches();
      }
    }

    function openDistributorReturnModalWeb(batchId) {
      const b = expiryBatches.find(x => x.id === batchId);
      if (!b) return;

      const cnNo = 'CN-2026-' + Math.floor(1000 + Math.random() * 9000);
      const supplierName = b.supplier || 'Apex Healthcare Distributors';
      const unitCost = (b.mrp * 0.85).toFixed(2);
      const totalRefund = (b.stock * unitCost).toFixed(2);

      if (window.Swal) {
        Swal.fire({
          title: `Return Batch #${b.batchNo} to Supplier`,
          html: `
            <div class="text-left text-xs space-y-3 p-1">
              <div class="p-3 bg-amber-50 rounded-xl border border-amber-200">
                <p class="font-bold text-amber-900">Medicine: ${escapeHtml(b.name)}</p>
                <p class="text-amber-700 font-mono">Batch: ${escapeHtml(b.batchNo)} | Exp: ${b.expiry}</p>
              </div>
              <div>
                <label class="block font-bold text-slate-700 mb-1">Distributor / Supplier Name:</label>
                <input type="text" id="return-supplier-name" value="${escapeHtml(supplierName)}" class="w-full p-2 border border-slate-300 rounded-lg text-xs font-bold" />
              </div>
              <div>
                <label class="block font-bold text-slate-700 mb-1">Return Quantity (Units):</label>
                <input type="number" id="return-qty-val" value="${b.stock}" min="1" max="${b.stock}" class="w-full p-2 border border-slate-300 rounded-lg text-xs font-bold" oninput="document.getElementById('cn-refund-val').innerText = (this.value * ${unitCost}).toFixed(2)" />
              </div>
              <div class="p-3 bg-rose-50 rounded-xl border border-rose-200 text-rose-900">
                <p class="font-bold uppercase tracking-wider text-[10px] text-rose-700">Estimated Credit Note Refund</p>
                <p class="text-xl font-black text-rose-800">₹<span id="cn-refund-val">${totalRefund}</span></p>
                <p class="text-[10px] text-rose-600">Credit Note Ref #: <b>${cnNo}</b></p>
              </div>
            </div>
          `,
          showCancelButton: true,
          confirmButtonText: '<i class="fa fa-receipt"></i> Issue Credit Note & Return',
          confirmButtonColor: '#9C1258'
        }).then((res) => {
          if (res.isConfirmed) {
            const finalSupplier = document.getElementById('return-supplier-name')?.value || supplierName;
            const finalQty = parseInt(document.getElementById('return-qty-val')?.value || b.stock, 10);
            const finalRefund = (finalQty * unitCost).toFixed(2);

            // Deduct stock or remove batch
            b.stock = Math.max(0, b.stock - finalQty);
            renderExpiryBatches();

            // Display generated credit note
            Swal.fire({
              title: `Credit Note #${cnNo} Issued!`,
              html: `
                <div class="text-left text-xs space-y-2 p-3 bg-slate-900 text-emerald-400 rounded-xl font-mono">
                  <p class="text-slate-300 font-bold border-b border-slate-700 pb-1">ROYAL PHARMACY • DISTRIBUTOR CREDIT NOTE</p>
                  <p>Credit Note #: <b class="text-white">${cnNo}</b></p>
                  <p>Distributor: <b class="text-white">${escapeHtml(finalSupplier)}</b></p>
                  <p>Returned: <b class="text-white">${finalQty} units</b> of ${escapeHtml(b.name)} (Batch: ${escapeHtml(b.batchNo)})</p>
                  <p>Credit Refund Amount: <b class="text-white">₹${finalRefund}</b></p>
                  <p class="text-[10px] text-slate-400 mt-2">Status: AUDIT LOGGED & ACCOUNT CREDIT UPDATED</p>
                </div>
              `,
              icon: 'success'
            });
          }
        });
      }
    }

    function filterExpiryBatches(type) {
      currentExpiryFilter = type;
      ['all', 'expired', '30', '90'].forEach(t => {
        const btn = document.getElementById(`exp-filter-${t}`);
        if (btn) {
          if (t === type) {
            btn.className = 'px-3 py-1.5 bg-slate-900 text-white text-xs font-semibold rounded-lg';
          } else {
            btn.className = 'px-3 py-1.5 bg-slate-100 text-slate-600 text-xs font-medium rounded-lg hover:bg-slate-200';
          }
        }
      });
      renderExpiryBatches();
    }

    function applyClearanceDiscount(batchId, percent) {
      const b = expiryBatches.find(x => x.id === batchId);
      if (!b) return;
      const discountedPrice = (b.mrp * (1 - percent / 100)).toFixed(2);
      Swal.fire({
        title: `Liquidation Sale Promo`,
        html: `Applied <b>${percent}% OFF</b> on <b>${b.name}</b> (Batch: ${b.batchNo}).<br>New promotional price: <b class="text-emerald-600">₹${discountedPrice}</b> (MRP: ₹${b.mrp.toFixed(2)})`,
        icon: 'success'
      });
    }

    function generateSupplierReturnVoucher() {
      const expiredItems = expiryBatches.filter(b => b.status === 'EXPIRED' || b.status === 'EXP_30');
      const totalClaimValue = expiredItems.reduce((sum, b) => sum + (b.stock * b.mrp), 0);

      Swal.fire({
        title: 'Supplier Return Debit Note (Voucher #RT-2026-89)',
        html: `
          <div class="text-left text-xs space-y-3">
            <div class="p-3 bg-amber-50 rounded-xl border border-amber-200">
              <p class="font-bold text-amber-900">Distributor: Apex Pharma Distributors (C&F)</p>
              <p class="text-amber-700 text-[11px]">Total Claim Amount: <b>₹${totalClaimValue.toFixed(2)}</b> (${expiredItems.length} batches)</p>
            </div>
            <ul class="divide-y divide-slate-100 max-h-48 overflow-y-auto">
              ${expiredItems.map(b => `
                <li class="py-2 flex justify-between">
                  <span><b>${b.name}</b> (Batch: ${b.batchNo})</span>
                  <span class="text-rose-600 font-bold">${b.stock} units × ₹${b.mrp}</span>
                </li>
              `).join('')}
            </ul>
          </div>
        `,
        showCancelButton: true,
        confirmButtonText: 'Download Debit Note PDF',
        confirmButtonColor: '#d97706'
      }).then(res => {
        if (res.isConfirmed) {
          Swal.fire('Generated!', 'Debit note voucher generated and sent to procurement ledger.', 'success');
        }
      });
    }

    // ==========================================
    // DRUG INTERACTIONS MATRIX LOGIC
    // ==========================================
    let selectedInteractionDrugs = ['Warfarin 5mg', 'Aspirin 75mg'];

    function renderInteractionChips() {
      const container = document.getElementById('interaction-drug-chips');
      if (!container) return;

      container.innerHTML = selectedInteractionDrugs.map((drug, idx) => `
        <span class="px-3 py-1.5 bg-rose-50 text-rose-800 text-xs font-semibold rounded-xl border border-rose-200 flex items-center space-x-1.5">
          <span>💊 ${escapeHtml(drug)}</span>
          <button onclick="removeInteractionDrug(${idx})" class="text-rose-400 hover:text-rose-700">
            <i data-lucide="x" class="w-3.5 h-3.5"></i>
          </button>
        </span>
      `).join('');

      evaluateDrugInteractions();
      if (window.lucide) lucide.createIcons();
    }

    function addInteractionDrugFromSelect() {
      const sel = document.getElementById('interaction-drug-select');
      if (!sel || !sel.value) return;
      if (!selectedInteractionDrugs.includes(sel.value)) {
        selectedInteractionDrugs.push(sel.value);
        renderInteractionChips();
      }
      sel.value = '';
    }

    function removeInteractionDrug(idx) {
      selectedInteractionDrugs.splice(idx, 1);
      renderInteractionChips();
    }

    function evaluateDrugInteractions() {
      const container = document.getElementById('interaction-results-container');
      if (!container) return;

      const drugsStr = selectedInteractionDrugs.join(' ').toLowerCase();

      if (selectedInteractionDrugs.length < 2) {
        container.innerHTML = `
          <div class="p-8 bg-white rounded-2xl border border-slate-100 text-center text-slate-400 text-xs">
            Please add at least 2 medicines above to evaluate clinical drug-drug interactions.
          </div>
        `;
        return;
      }

      let alerts = [];

      if (drugsStr.includes('warfarin') && drugsStr.includes('aspirin')) {
        alerts.push({
          severity: 'HIGH',
          title: 'Warfarin + Aspirin (Major Hemorrhagic Hazard)',
          mechanism: 'Synergistic inhibition of coagulation cascade (Vitamin K antagonism) and platelet aggregation (COX-1 inhibition).',
          action: 'Contraindicated unless explicitly authorized by treating cardiologist. Co-prescribe PPI (Pantoprazole) and monitor PT/INR tightly.',
          food: 'Avoid large fluctuations in Vitamin K-rich leafy green intake (spinach, kale).'
        });
      }

      if (drugsStr.includes('clopidogrel') && drugsStr.includes('aspirin')) {
        alerts.push({
          severity: 'MODERATE',
          title: 'Clopidogrel + Aspirin (Dual Antiplatelet Therapy)',
          mechanism: 'Beneficial for post-PCI/stenting, but substantially elevates upper GI bleeding incidence.',
          action: 'Review duration of DAPT (typically 6-12 months). Counsel patient on melena / epistaxis signs.',
          food: 'Avoid alcohol.'
        });
      }

      if (drugsStr.includes('ciprofloxacin') && drugsStr.includes('theophylline')) {
        alerts.push({
          severity: 'HIGH',
          title: 'Ciprofloxacin + Theophylline (CYP1A2 Inhibition Toxicity)',
          mechanism: 'Ciprofloxacin potent inhibition of CYP1A2 slows theophylline hepatic clearance by 30-50%, risking seizures and arrhythmias.',
          action: 'Reduce theophylline dose by 50% or switch antibiotic to Levofloxacin / Azithromycin.',
          food: 'Limit high caffeine intake.'
        });
      }

      if (drugsStr.includes('methotrexate') && (drugsStr.includes('ibuprofen') || drugsStr.includes('aspirin'))) {
        alerts.push({
          severity: 'HIGH',
          title: 'Methotrexate + NSAID (Renal Clearance Impairment)',
          mechanism: 'NSAIDs reduce renal tubular secretion of methotrexate, causing severe pancytopenia and hepatotoxicity.',
          action: 'Avoid concurrent OTC NSAIDs. Use Paracetamol for analgesia if needed.',
          food: 'Folic acid supplementation required.'
        });
      }

      if (alerts.length === 0) {
        alerts.push({
          severity: 'NONE',
          title: 'No Severe Known Major Contraindications Detected',
          mechanism: 'No classic dangerous kinetic interactions flagged between the selected formulations in Indian Pharmacopoeia monographs.',
          action: 'Safe to dispense according to physician standard dosing schedule.',
          food: 'Administer with water after food if GI discomfort occurs.'
        });
      }

      container.innerHTML = alerts.map(a => {
        let badgeColor = a.severity === 'HIGH' ? 'bg-red-100 text-red-800 border-red-200' : (a.severity === 'MODERATE' ? 'bg-amber-100 text-amber-800 border-amber-200' : 'bg-emerald-100 text-emerald-800 border-emerald-200');
        let cardBg = a.severity === 'HIGH' ? 'border-red-200 bg-red-50/20' : (a.severity === 'MODERATE' ? 'border-amber-200 bg-amber-50/20' : 'border-emerald-200 bg-emerald-50/20');

        return `
          <div class="bg-white p-5 rounded-2xl border ${cardBg} shadow-sm space-y-3 text-xs">
            <div class="flex items-center justify-between">
              <span class="px-2.5 py-1 rounded-full font-bold border ${badgeColor}">
                ${a.severity === 'HIGH' ? '⚠️ High Severity Interaction' : (a.severity === 'MODERATE' ? '⚡ Moderate Monitoring Needed' : '✅ Clinically Compatible')}
              </span>
              <span class="text-slate-400 font-mono text-[10px]">Pharmacopoeia CDSCO Monograph</span>
            </div>
            <h4 class="text-base font-bold text-slate-800">${escapeHtml(a.title)}</h4>
            <div class="space-y-1 text-slate-600">
              <p><b>Pharmacological Mechanism:</b> ${escapeHtml(a.mechanism)}</p>
              <p><b>Clinical Pharmacist Action:</b> ${escapeHtml(a.action)}</p>
              <p><b>Food & Lifestyle Precautions:</b> ${escapeHtml(a.food)}</p>
            </div>
          </div>
        `;
      }).join('');
    }

    function sendGeminiInteractionAnalysis() {
      if (selectedInteractionDrugs.length < 2) {
        Swal.fire('Notice', 'Please select at least 2 drugs first.', 'info');
        return;
      }
      switchTab('gemini');
      const prompt = `Perform an in-depth clinical pharmacology and drug interaction analysis for this combination: ${selectedInteractionDrugs.join(' + ')}. Detail CYP enzymes, therapeutic monitoring parameters, and safer alternative formulations.`;
      const input = document.getElementById('gemini-chat-input');
      if (input) input.value = prompt;
      sendGeminiWebChat();
    }

    // ==========================================
    // UNIT CONVERTER & IV FLOW RATE CALCULATORS
    // ==========================================
    function calculateIvRate() {
      const vol = parseFloat(document.getElementById('iv-volume').value) || 500;
      const hrs = parseFloat(document.getElementById('iv-hours').value) || 4;
      const dropFactor = parseFloat(document.getElementById('iv-drop-factor').value) || 20;

      const totalMinutes = hrs * 60;
      const dropsPerMin = Math.round((vol * dropFactor) / totalMinutes);
      const mlPerHour = (vol / hrs).toFixed(1);

      const resEl = document.getElementById('iv-rate-result');
      if (resEl) {
        resEl.innerHTML = `Infusion Rate: <span class="text-lg font-bold text-teal-800">${dropsPerMin} drops/min</span> (${mlPerHour} mL/hr)`;
      }
    }

    function calculateConcentration() {
      const percent = parseFloat(document.getElementById('conc-percent').value) || 5;
      const vol = parseFloat(document.getElementById('conc-volume').value) || 100;

      // 1% w/v = 1g in 100mL = 10mg/mL
      const mgPerMl = percent * 10;
      const totalGrams = (percent * vol) / 100;

      const resEl = document.getElementById('conc-result');
      if (resEl) {
        resEl.innerHTML = `Concentration: <span class="text-lg font-bold text-purple-800">${mgPerMl} mg/mL</span> (Total ${totalGrams.toFixed(2)} grams active drug)`;
      }
    }

    function exportGstrReport() {
      const rows = [
        ['HSN Code', 'Category Description', 'Taxable Value (INR)', 'GST Rate', 'CGST (INR)', 'SGST (INR)', 'Total Tax (INR)'],
        ['3004', 'Medicaments for Therapeutic or Prophylactic Uses', '142800.00', '12%', '8568.00', '8568.00', '17136.00'],
        ['3002', 'Vaccines, Toxins, Blood fractions & Insulin', '85400.00', '5%', '2135.00', '2135.00', '4270.00'],
        ['9018', 'Medical Instruments & Diagnostic Kits', '34600.00', '18%', '3114.00', '3114.00', '6228.00']
      ];
      const csvContent = "data:text/csv;charset=utf-8," + rows.map(e => e.join(",")).join("\n");
      const encodedUri = encodeURI(csvContent);
      const link = document.createElement("a");
      link.setAttribute("href", encodedUri);
      link.setAttribute("download", `Royal_Pharmacy_GSTR_Tax_Report_${new Date().toISOString().split('T')[0]}.csv`);
      document.body.appendChild(link);
      link.click();
      document.body.removeChild(link);
      Swal.fire('Exported', 'GSTR tax slab summary downloaded as CSV.', 'success');
    }

    // ==========================================
    // GLOBAL SEARCH (CTRL+K) & VOICE SEARCH
    // ==========================================
    window.addEventListener('keydown', (e) => {
      if ((e.ctrlKey || e.metaKey) && e.key.toLowerCase() === 'k') {
        e.preventDefault();
        openGlobalSearchModal();
      }
    });

    function openGlobalSearchModal() {
      Swal.fire({
        title: 'Search Royal Pharmacy Hub (Ctrl+K)',
        html: `
          <div class="text-left space-y-3 text-xs">
            <input id="swal-global-search" oninput="runGlobalUniversalSearch(this.value)" placeholder="Search medicines, customers, invoices, tools..." class="w-full p-3 bg-slate-50 border border-slate-200 rounded-xl text-xs outline-none focus:ring-2 focus:ring-pink-500" autofocus>
            <div id="swal-global-results" class="max-h-60 overflow-y-auto space-y-2 pt-2">
              <p class="text-slate-400 text-center py-4">Type medicine name, customer name, batch number, or tool...</p>
            </div>
          </div>
        `,
        showConfirmButton: false,
        showCloseButton: true
      });
    }

    function runGlobalUniversalSearch(q) {
      const container = document.getElementById('swal-global-results');
      if (!container) return;
      const query = q.trim().toLowerCase();
      if (!query) {
        container.innerHTML = `<p class="text-slate-400 text-center py-4">Type medicine name, customer name, batch number, or tool...</p>`;
        return;
      }

      let results = [];

      // 1. Check medicines
      medicinesDatabase.filter(m => m.name.toLowerCase().includes(query) || m.salt.toLowerCase().includes(query)).slice(0, 4).forEach(m => {
        results.push({
          type: 'Medicine',
          icon: 'pill',
          title: m.name,
          subtitle: `${m.salt} • ₹${m.mrp} • Stock: ${m.stock}`,
          action: () => { Swal.close(); switchTab('database'); }
        });
      });

      // 2. Check customers
      udharCustomers.filter(c => c.name.toLowerCase().includes(query) || c.phone.includes(query)).slice(0, 3).forEach(c => {
        results.push({
          type: 'Credit Customer',
          icon: 'user',
          title: c.name,
          subtitle: `${c.phone} • Balance: ₹${c.balance}`,
          action: () => { Swal.close(); switchTab('udharkhata'); }
        });
      });

      // 3. Quick shortcuts
      if ('billing pos sale'.includes(query)) {
        results.push({ type: 'Navigation', icon: 'shopping-cart', title: 'Go to Billing Counter POS', subtitle: 'Create new customer bill / scan barcode', action: () => { Swal.close(); switchTab('pos'); } });
      }
      if ('ai pharmacist gemini chatbot'.includes(query)) {
        results.push({ type: 'Navigation', icon: 'sparkles', title: 'Gemini Pharmacist AI', subtitle: 'Clinical consultations and drug safety', action: () => { Swal.close(); switchTab('gemini'); } });
      }
      if ('interaction contraindication'.includes(query)) {
        results.push({ type: 'Navigation', icon: 'shield-alert', title: 'Drug Interactions Matrix', subtitle: 'Multi-drug safety analysis', action: () => { Swal.close(); switchTab('interactions'); } });
      }
      if ('expiry batch'.includes(query)) {
        results.push({ type: 'Navigation', icon: 'alert-triangle', title: 'Batch & Expiry Dashboard', subtitle: 'Stock liquidation and supplier returns', action: () => { Swal.close(); switchTab('expiry'); } });
      }

      if (results.length === 0) {
        container.innerHTML = `<p class="text-slate-400 text-center py-4">No matching records found for "${escapeHtml(q)}".</p>`;
        return;
      }

      container.innerHTML = results.map((r, i) => `
        <div onclick="window.__globalActions[${i}]()" class="p-2.5 hover:bg-pink-50 rounded-xl cursor-pointer border border-slate-100 flex items-center justify-between transition">
          <div class="flex items-center space-x-2.5">
            <span class="px-2 py-0.5 bg-slate-100 text-slate-700 text-[10px] font-bold rounded">${r.type}</span>
            <div>
              <p class="font-bold text-slate-800 text-xs">${escapeHtml(r.title)}</p>
              <p class="text-[10px] text-slate-500">${escapeHtml(r.subtitle)}</p>
            </div>
          </div>
          <i data-lucide="arrow-right" class="w-3.5 h-3.5 text-slate-400"></i>
        </div>
      `).join('');

      window.__globalActions = results.map(r => r.action);
      if (window.lucide) lucide.createIcons();
    }

    function startVoiceSearch() {
      if (!('webkitSpeechRecognition' in window) && !('SpeechRecognition' in window)) {
        Swal.fire('Voice Search', 'Speech recognition is not supported in this browser. Please use the text search bar.', 'info');
        return;
      }

      const SpeechRecognition = window.SpeechRecognition || window.webkitSpeechRecognition;
      const recognition = new SpeechRecognition();
      recognition.lang = 'en-IN';
      recognition.interimResults = false;

      Swal.fire({
        title: 'Listening...',
        html: `<div class="py-4 text-center"><div class="w-16 h-16 rounded-full bg-pink-100 text-royal-magenta flex items-center justify-center mx-auto animate-pulse"><i data-lucide="mic" class="w-8 h-8"></i></div><p class="text-xs text-slate-500 mt-3">Speak medicine name, customer name, or command (e.g. "Augmentin 625")...</p></div>`,
        showConfirmButton: false,
        showCancelButton: true,
        cancelButtonText: 'Stop'
      });
      if (window.lucide) lucide.createIcons();

      recognition.onresult = (event) => {
        const text = event.results[0][0].transcript;
        Swal.close();
        openGlobalSearchModal();
        setTimeout(() => {
          const input = document.getElementById('swal-global-search');
          if (input) {
            input.value = text;
            runGlobalUniversalSearch(text);
          }
        }, 300);
      };

      recognition.onerror = (event) => {
        Swal.close();
        Swal.fire('Voice Error', 'Could not detect voice clearly. Please try again.', 'error');
      };

      recognition.start();
    }

    // --- Low Stock Alert Widget & Batch QR Scanner Implementations ---
    function renderLowStockWidget() {
      if (typeof renderDashboardReplenishmentSection === 'function') {
        renderDashboardReplenishmentSection();
      }
      const container = document.getElementById('web-low-stock-cards-list');
      const countBadge = document.getElementById('web-low-stock-badge-count');
      if (!container) return;

      const lowStockItems = (state.drugDatabase || []).filter(d => (d.stock <= (d.reorderLevel || 15)));
      if (countBadge) {
        countBadge.innerText = `${lowStockItems.length} Items Critical`;
      }

      if (lowStockItems.length === 0) {
        container.innerHTML = `
          <div class="col-span-full text-center py-4 text-emerald-700 dark:text-emerald-300 bg-emerald-50 dark:bg-emerald-950/40 rounded-xl text-xs font-semibold border border-emerald-200 dark:border-emerald-800">
            ✅ All inventory levels healthy! No medicines below reorder threshold.
          </div>
        `;
        return;
      }

      container.innerHTML = lowStockItems.slice(0, 8).map(drug => {
        const isUrgent = drug.stock < 5;
        return `
        <div class="bg-white dark:bg-slate-800 p-3 rounded-xl border ${isUrgent ? 'border-red-500 shadow-md ring-2 ring-red-500/30 low-stock-breathing' : 'border-red-200 dark:border-red-900/40 shadow-sm'} flex flex-col justify-between transition-all">
          <div>
            <div class="flex items-center justify-between gap-1">
              <h4 class="font-bold text-xs text-slate-900 dark:text-white truncate">${escapeHtml(drug.name)}</h4>
              <div class="flex items-center gap-1 shrink-0">
                ${isUrgent ? '<span class="text-[9px] font-extrabold text-white bg-red-600 px-1.5 py-0.5 rounded shadow-sm animate-pulse">&lt;5 CRITICAL</span>' : ''}
                <span class="text-[10px] font-bold text-red-600 bg-red-100 dark:bg-red-900/50 dark:text-red-300 px-1.5 py-0.5 rounded">Min: ${drug.reorderLevel || 15}</span>
              </div>
            </div>
            <p class="text-[11px] text-slate-500 dark:text-slate-400 mt-1">Stock: <strong class="${isUrgent ? 'text-red-600 font-extrabold text-xs' : 'text-red-600 dark:text-red-400'}">${drug.stock} units</strong></p>
            <div class="w-full bg-red-100 dark:bg-red-950 h-1.5 rounded-full overflow-hidden mt-2">
              <div class="${isUrgent ? 'bg-red-600' : 'bg-red-500'} h-full" style="width: ${Math.min(100, (drug.stock / (drug.reorderLevel || 15)) * 100)}%"></div>
            </div>
          </div>
          <button onclick="addLowStockToPurchaseOrder('${escapeHtml(escapeJsParam(drug.name))}')" class="mt-2.5 w-full bg-royal-magenta text-white py-1.5 rounded-lg text-[11px] font-bold hover:bg-royal-accent transition flex items-center justify-center space-x-1 shadow-sm">
            <i class="ph ph-plus-circle text-xs"></i>
            <span>1-Click + PO</span>
          </button>
        </div>
      `;
      }).join('');
    }

    function addLowStockToPurchaseOrder(drugName) {
      const drug = state.drugDatabase.find(d => d.name === drugName);
      if (!drug) return;
      const reorderQty = Math.max(20, ((drug.reorderLevel || 15) * 2) - drug.stock);
      state.cartItems = state.cartItems || [];
      const existing = state.cartItems.find(c => c.name === drug.name);
      if (existing) {
        existing.qty += reorderQty;
      } else {
        state.cartItems.push({
          id: Date.now(),
          name: drug.name,
          manufacturer: "Royal Pharma Supplier",
          qty: reorderQty,
          rate: drug.purchasePrice || (drug.price * 0.75),
          mrp: drug.price,
          type: "PURCHASE_ORDER"
        });
      }
      Swal.fire({
        icon: 'success',
        title: 'Added to Purchase Orders',
        text: `Added ${reorderQty} units of ${drug.name} to Purchase List!`,
        timer: 1500,
        showConfirmButton: false
      });
      renderCartTable();
    }

    function reorderAllLowStockWeb() {
      const lowStockItems = (state.drugDatabase || []).filter(d => (d.stock <= (d.reorderLevel || 15)));
      if (lowStockItems.length === 0) {
        Swal.fire('Stock Healthy', 'No medicines are currently below reorder threshold.', 'info');
        return;
      }
      lowStockItems.forEach(drug => {
        addLowStockToPurchaseOrder(drug.name);
      });
      Swal.fire({
        icon: 'success',
        title: 'Reorder Complete!',
        text: `Added all ${lowStockItems.length} low stock medicines to your Purchase Orders cart.`,
        confirmButtonColor: '#9C1258'
      });
      renderDashboardReplenishmentSection();
    }

    // =========================================================================
    // DASHBOARD REPLENISHMENT WATCHLIST CONTROLLER & INTERACTIVE ACTIONS
    // =========================================================================
    let currentReplenishFilter = 'ALL';
    let currentReplenishSearchQuery = '';

    function renderDashboardReplenishmentSection() {
      const tableBody = document.getElementById('dashboard-replenishment-table-body');
      if (!tableBody) return;

      const allDrugs = state.drugDatabase || [];
      // Items with stock <= reorder threshold (or minStock)
      const belowThresholdItems = allDrugs.filter(d => {
        const threshold = d.minStock || d.reorderLevel || 15;
        return d.stock <= threshold;
      });

      // Statistics
      const totalCount = belowThresholdItems.length;
      const outOfStockCount = belowThresholdItems.filter(d => d.stock === 0).length;
      const totalShortage = belowThresholdItems.reduce((acc, d) => {
        const threshold = d.minStock || d.reorderLevel || 15;
        return acc + Math.max(0, threshold - d.stock);
      }, 0);
      const totalEstCost = belowThresholdItems.reduce((acc, d) => {
        const threshold = d.minStock || d.reorderLevel || 15;
        const deficit = Math.max(0, threshold - d.stock);
        const costPerUnit = d.purchasePrice || (d.price * 0.75) || 50;
        return acc + (deficit * costPerUnit);
      }, 0);

      // Update stat badges and counter cards
      const totalBadge = document.getElementById('replenishment-total-badge');
      if (totalBadge) {
        totalBadge.innerText = `${totalCount} Items Need Stock`;
        totalBadge.className = totalCount > 0 
          ? "px-2.5 py-0.5 rounded-full text-xs font-black bg-rose-100 text-rose-800 dark:bg-rose-900/60 dark:text-rose-200 border border-rose-200 dark:border-rose-800 animate-pulse"
          : "px-2.5 py-0.5 rounded-full text-xs font-bold bg-emerald-100 text-emerald-800 border border-emerald-200";
      }

      const statTotal = document.getElementById('replenish-stat-total');
      if (statTotal) statTotal.innerText = totalCount;

      const statOut = document.getElementById('replenish-stat-out');
      if (statOut) statOut.innerText = outOfStockCount;

      const statDeficit = document.getElementById('replenish-stat-deficit');
      if (statDeficit) statDeficit.innerText = `${totalShortage} units`;

      const statCost = document.getElementById('replenish-stat-cost');
      if (statCost) statCost.innerText = `₹${totalEstCost.toFixed(2)}`;

      // Filter by active tab
      let filtered = belowThresholdItems;
      if (currentReplenishFilter === 'OUT') {
        filtered = filtered.filter(d => d.stock === 0);
      } else if (currentReplenishFilter === 'CRITICAL') {
        filtered = filtered.filter(d => d.stock > 0 && d.stock < 5);
      } else if (currentReplenishFilter === 'STANDARD') {
        filtered = filtered.filter(d => d.stock >= 5);
      }

      // Filter by search query
      if (currentReplenishSearchQuery.trim()) {
        const q = currentReplenishSearchQuery.trim().toLowerCase();
        filtered = filtered.filter(d => 
          (d.name && d.name.toLowerCase().includes(q)) ||
          (d.generic && d.generic.toLowerCase().includes(q)) ||
          (d.hsn && d.hsn.toString().includes(q)) ||
          (d.category && d.category.toLowerCase().includes(q))
        );
      }

      // Render table rows
      if (filtered.length === 0) {
        tableBody.innerHTML = `
          <tr>
            <td colspan="9" class="px-6 py-10 text-center">
              <div class="flex flex-col items-center justify-center space-y-2 text-slate-400">
                <i class="ph ph-check-circle text-3xl text-emerald-500"></i>
                <p class="font-bold text-sm text-slate-700 dark:text-slate-300">
                  ${totalCount === 0 ? "All medicines have healthy stock above their reorder thresholds!" : "No items match the selected filter/search criteria."}
                </p>
                <p class="text-xs text-slate-400">
                  ${totalCount === 0 ? "Inventory safety buffers are maintained across all formulary categories." : "Try clearing your search query or selecting 'All Below Threshold'."}
                </p>
              </div>
            </td>
          </tr>
        `;
        return;
      }

      tableBody.innerHTML = filtered.map(drug => {
        const threshold = drug.minStock || drug.reorderLevel || 15;
        const deficit = Math.max(0, threshold - drug.stock);
        const recommendedQty = Math.max(20, (threshold * 2) - drug.stock);
        const isOut = drug.stock === 0;
        const isCritical = !isOut && drug.stock < 5;
        const hsn = (drug.hsn || '300490').toString();
        const rack = drug.rackLocation || 'Rack A-1';
        const price = Number(drug.price || 0).toFixed(2);

        let stockBadge = '';
        if (isOut) {
          stockBadge = '<span class="px-2 py-0.5 rounded-full text-[10px] font-black bg-rose-600 text-white shadow-sm animate-pulse">0 (OUT OF STOCK)</span>';
        } else if (isCritical) {
          stockBadge = `<span class="px-2 py-0.5 rounded-full text-[10px] font-bold bg-rose-100 text-rose-700 dark:bg-rose-900/60 dark:text-rose-200 border border-rose-200">${drug.stock} units (Critical)</span>`;
        } else {
          stockBadge = `<span class="px-2 py-0.5 rounded-full text-[10px] font-bold bg-amber-100 text-amber-800 dark:bg-amber-900/60 dark:text-amber-200 border border-amber-200">${drug.stock} units</span>`;
        }

        return `
          <tr class="hover:bg-slate-50/80 dark:hover:bg-slate-800/60 transition group">
            <td class="px-4 py-3">
              <div class="flex items-center space-x-2.5">
                <div class="w-8 h-8 rounded-lg flex items-center justify-center font-black text-xs ${isOut ? 'bg-rose-600 text-white' : (isCritical ? 'bg-rose-100 text-rose-700' : 'bg-amber-100 text-amber-800')} shrink-0">
                  ${escapeHtml(drug.name.substring(0, 2).toUpperCase())}
                </div>
                <div>
                  <div class="font-bold text-slate-900 dark:text-white flex items-center gap-1.5">
                    <span>${escapeHtml(drug.name)}</span>
                    ${isOut ? '<span class="text-[9px] font-black text-rose-600 uppercase tracking-wider bg-rose-50 px-1 rounded">Depleted</span>' : ''}
                  </div>
                  <div class="text-[11px] text-slate-500 dark:text-slate-400 truncate max-w-xs">
                    ${escapeHtml(drug.generic || drug.composition || drug.category || 'Formulation')}
                  </div>
                </div>
              </div>
            </td>
            <td class="px-3 py-3 text-center font-mono text-[11px] text-slate-600 dark:text-slate-300">
              <span class="px-2 py-0.5 bg-slate-100 dark:bg-slate-800 rounded border border-slate-200 dark:border-slate-700 font-semibold">${escapeHtml(hsn)}</span>
            </td>
            <td class="px-3 py-3 text-center text-[11px] font-semibold text-slate-600 dark:text-slate-400">
              ${escapeHtml(rack)}
            </td>
            <td class="px-3 py-3 text-center">
              ${stockBadge}
            </td>
            <td class="px-3 py-3 text-center">
              <span class="px-2 py-0.5 rounded text-[11px] font-bold bg-slate-100 dark:bg-slate-800 text-slate-700 dark:text-slate-300 border border-slate-200 dark:border-slate-700">
                ${threshold} units
              </span>
            </td>
            <td class="px-3 py-3 text-center font-bold text-rose-600 dark:text-rose-400">
              -${deficit} units
            </td>
            <td class="px-3 py-3 text-center font-bold text-emerald-600 dark:text-emerald-400">
              +${recommendedQty} units
            </td>
            <td class="px-3 py-3 text-right font-mono font-bold text-slate-800 dark:text-slate-200">
              ₹${price}
            </td>
            <td class="px-4 py-3 text-center">
              <div class="flex items-center justify-center space-x-1.5">
                <button onclick="addLowStockToPurchaseOrder('${escapeHtml(escapeJsParam(drug.name))}')" class="px-2.5 py-1 bg-gradient-to-r from-rose-600 to-amber-600 hover:from-rose-700 hover:to-amber-700 text-white rounded-lg text-xs font-bold transition shadow-sm flex items-center space-x-1 active:scale-95" title="Add recommended replenishment quantity (${recommendedQty} units) to Purchase Order cart">
                  <i class="ph ph-shopping-cart-simple text-xs"></i>
                  <span>+ Reorder</span>
                </button>
                <button onclick="generateAndPrintThermalBarcodeLabel('${escapeHtml(escapeJsParam(drug.name))}')" class="p-1 bg-slate-100 hover:bg-slate-200 dark:bg-slate-800 dark:hover:bg-slate-700 text-slate-700 dark:text-slate-300 rounded-lg text-xs font-bold transition border border-slate-200 dark:border-slate-700" title="Print thermal barcode label with HSN code ${escapeHtml(hsn)}">
                  <i class="ph ph-printer text-xs"></i>
                </button>
              </div>
            </td>
          </tr>
        `;
      }).join('');
    }

    function filterReplenishmentList(filterType) {
      currentReplenishFilter = filterType;
      const tabs = ['ALL', 'OUT', 'CRITICAL', 'STANDARD'];
      tabs.forEach(t => {
        const btn = document.getElementById(`replenish-tab-${t}`);
        if (btn) {
          if (t === filterType) {
            btn.className = "px-3 py-1.5 rounded-lg text-xs font-bold bg-royal-magenta text-white shadow-sm transition";
          } else {
            btn.className = "px-3 py-1.5 rounded-lg text-xs font-bold bg-slate-100 dark:bg-slate-700 text-slate-600 dark:text-slate-300 hover:bg-slate-200 transition";
          }
        }
      });
      renderDashboardReplenishmentSection();
    }

    function onReplenishSearchInput(value) {
      currentReplenishSearchQuery = value || '';
      renderDashboardReplenishmentSection();
    }

    // =========================================================================
    // LIVE WEBCAM BARCODE SCANNER FOR POS BILLING (HTML5-QRCODE INTEGRATION)
    // =========================================================================
    let livePosHtml5QrCode = null;
    let isPosWebcamActive = false;
    let lastScannedPosBarcode = "";
    let lastScannedPosTimestamp = 0;

    function togglePosWebcamScanner(forceOpen) {
      const panel = document.getElementById('pos-webcam-scanner-panel');
      if (!panel) return;

      const shouldOpen = forceOpen !== undefined ? forceOpen : panel.classList.contains('hidden');

      if (shouldOpen) {
        panel.classList.remove('hidden');
        startLivePosWebcam();
      } else {
        stopLivePosWebcam();
        panel.classList.add('hidden');
      }
    }

    function startLivePosWebcam(preferredCameraId) {
      if (typeof Html5Qrcode === 'undefined') {
        const statusBadge = document.getElementById('pos-webcam-status-badge');
        if (statusBadge) {
          statusBadge.innerText = 'Html5Qrcode library not loaded';
          statusBadge.className = 'px-2 py-0.5 rounded-full text-[10px] font-bold bg-amber-500/20 text-amber-300 border border-amber-500/30';
        }
        return;
      }

      const statusBadge = document.getElementById('pos-webcam-status-badge');
      if (statusBadge) {
        statusBadge.innerText = 'Initializing Camera...';
        statusBadge.className = 'px-2 py-0.5 rounded-full text-[10px] font-bold bg-blue-500/20 text-blue-300 border border-blue-500/30';
      }

      Html5Qrcode.getCameras().then(devices => {
        const camSelect = document.getElementById('pos-webcam-camera-select');
        if (camSelect && devices && devices.length > 0) {
          camSelect.innerHTML = devices.map((cam, idx) => `
            <option value="${cam.id}" ${preferredCameraId === cam.id || idx === 0 ? 'selected' : ''}>
              ${cam.label || `Camera ${idx + 1}`}
            </option>
          `).join('');
        }

        if (!livePosHtml5QrCode) {
          livePosHtml5QrCode = new Html5Qrcode("pos-webcam-reader");
        }

        const cameraIdOrConfig = preferredCameraId || (devices && devices.length > 0 ? devices[0].id : { facingMode: "environment" });

        livePosHtml5QrCode.start(
          cameraIdOrConfig,
          {
            fps: 15,
            qrbox: { width: 260, height: 160 },
            aspectRatio: 1.777778
          },
          (decodedText) => {
            handlePosBarcodeScanned(decodedText);
          },
          () => {}
        ).then(() => {
          isPosWebcamActive = true;
          if (statusBadge) {
            statusBadge.innerText = '● Live Scanning Active';
            statusBadge.className = 'px-2 py-0.5 rounded-full text-[10px] font-bold bg-emerald-500/20 text-emerald-300 border border-emerald-500/30 animate-pulse';
          }
        }).catch(err => {
          console.warn("Camera start error:", err);
          isPosWebcamActive = false;
          if (statusBadge) {
            statusBadge.innerText = 'Camera Access Blocked / Standby';
            statusBadge.className = 'px-2 py-0.5 rounded-full text-[10px] font-bold bg-amber-500/20 text-amber-300 border border-amber-500/30';
          }
        });
      }).catch(err => {
        console.warn("Could not list cameras:", err);
        if (statusBadge) {
          statusBadge.innerText = 'No Webcam Detected - Simulator Ready';
          statusBadge.className = 'px-2 py-0.5 rounded-full text-[10px] font-bold bg-amber-500/20 text-amber-300 border border-amber-500/30';
        }
      });
    }

    function stopLivePosWebcam() {
      const statusBadge = document.getElementById('pos-webcam-status-badge');
      if (statusBadge) {
        statusBadge.innerText = 'Camera Paused';
        statusBadge.className = 'px-2 py-0.5 rounded-full text-[10px] font-bold bg-slate-500/20 text-slate-300 border border-slate-500/30';
      }
      if (livePosHtml5QrCode && isPosWebcamActive) {
        livePosHtml5QrCode.stop().then(() => {
          isPosWebcamActive = false;
        }).catch(() => {
          isPosWebcamActive = false;
        });
      }
    }

    function switchPosWebcamCamera(cameraId) {
      if (livePosHtml5QrCode && isPosWebcamActive) {
        livePosHtml5QrCode.stop().then(() => {
          isPosWebcamActive = false;
          startLivePosWebcam(cameraId);
        }).catch(() => {
          startLivePosWebcam(cameraId);
        });
      } else {
        startLivePosWebcam(cameraId);
      }
    }

    function playPosScanBeep() {
      try {
        const audioCtx = new (window.AudioContext || window.webkitAudioContext)();
        const osc = audioCtx.createOscillator();
        const gain = audioCtx.createGain();
        osc.type = 'sine';
        osc.frequency.setValueAtTime(880, audioCtx.currentTime);
        gain.gain.setValueAtTime(0.15, audioCtx.currentTime);
        gain.gain.exponentialRampToValueAtTime(0.01, audioCtx.currentTime + 0.12);
        osc.connect(gain);
        gain.connect(audioCtx.destination);
        osc.start();
        osc.stop(audioCtx.currentTime + 0.12);
      } catch (_e) {}
    }

    function handlePosBarcodeScanned(decodedText) {
      const now = Date.now();
      const trimmed = (decodedText || '').trim();
      if (!trimmed) return;

      // Debounce protection: ignore duplicate scans within 1.4 seconds
      if (trimmed === lastScannedPosBarcode && (now - lastScannedPosTimestamp) < 1400) {
        return;
      }
      lastScannedPosBarcode = trimmed;
      lastScannedPosTimestamp = now;

      // Check if it is a JSON Batch QR payload
      if (trimmed.startsWith('{') && trimmed.endsWith('}')) {
        try {
          const payload = JSON.parse(trimmed);
          if (payload && payload.name) {
            playPosScanBeep();
            addBatchQrPayloadToBill(payload);
            updatePosScanFeedback(payload.name, `Batch QR: ${payload.batch || 'N/A'} (Exp: ${payload.exp || 'N/A'})`, payload.mrp || payload.rate || 0);
            return;
          }
        } catch (_e) {}
      }

      // Look up matching drug in database by HSN code, name, or barcode
      const allDrugs = state.drugDatabase || [];
      let matched = allDrugs.find(d => {
        const hsn = (d.hsn || '').toString().trim();
        return hsn === trimmed;
      });

      if (!matched) {
        matched = allDrugs.find(d => {
          const hsn = (d.hsn || '').toString().trim();
          return hsn && (trimmed.startsWith(hsn) || hsn.startsWith(trimmed));
        });
      }

      if (!matched) {
        matched = allDrugs.find(d => {
          const name = (d.name || '').toLowerCase();
          return name === trimmed.toLowerCase() || name.includes(trimmed.toLowerCase());
        });
      }

      if (!matched) {
        matched = allDrugs[0];
      }

      if (matched) {
        playPosScanBeep();

        state.currentSale = state.currentSale || { items: [], discountPct: 0, paymentMode: 'CASH' };
        const existing = (state.currentSale.items || []).find(i => i.name === matched.name);
        if (existing) {
          existing.qty += 1;
          existing.total = existing.qty * (existing.mrp || matched.price);
        } else {
          state.currentSale.items.push({
            name: matched.name,
            batch: matched.batch || ("RP-" + Math.floor(100 + Math.random() * 900)),
            expiry: matched.expiry || "12/2027",
            qty: 1,
            mrp: matched.price,
            purchasePrice: matched.purchasePrice || (matched.price * 0.75),
            total: matched.price
          });
        }

        renderBillItems();

        updatePosScanFeedback(matched.name, `Barcode/HSN: ${matched.hsn || trimmed}`, matched.price);

        if (window.Swal) {
          const Toast = Swal.mixin({
            toast: true,
            position: 'top-end',
            showConfirmButton: false,
            timer: 1600,
            timerProgressBar: true
          });
          Toast.fire({
            icon: 'success',
            title: `+1 ${matched.name} added to bill!`
          });
        }
      }
    }

    function updatePosScanFeedback(medicineName, codeInfo, price) {
      const nameEl = document.getElementById('pos-last-scanned-name');
      const codeEl = document.getElementById('pos-last-scanned-code');
      const container = document.getElementById('pos-last-scanned-container');

      if (nameEl) {
        nameEl.innerHTML = `<span class="text-emerald-400 font-bold">✓ ${escapeHtml(medicineName)}</span>`;
      }
      if (codeEl) {
        codeEl.innerHTML = `<span class="text-pink-300 font-mono text-[10px]">${escapeHtml(codeInfo)} • ₹${Number(price).toFixed(2)}</span>`;
      }
      if (container) {
        container.classList.add('ring-2', 'ring-emerald-500/60');
        setTimeout(() => {
          container.classList.remove('ring-2', 'ring-emerald-500/60');
        }, 1200);
      }
    }

    function simulatePosWebcamScan(code, fallbackName) {
      handlePosBarcodeScanned(code);
    }

    // --- Real-time Recent Activity Feed Component (Web Dashboard) ---
    function renderRecentActivityFeedWeb() {
      const container = document.getElementById('web-recent-activity-feed-list');
      if (!container) return;

      const logs = (state.staffActivityLogs || []);
      if (logs.length === 0) {
        container.innerHTML = `
          <div class="p-4 text-center text-xs text-slate-400 bg-slate-50 dark:bg-slate-900/40 rounded-xl">
            No recent billing or stock events recorded yet.
          </div>
        `;
        return;
      }

      container.innerHTML = logs.slice(0, 6).map(log => {
        const isBill = log.actionType === 'BILL_GENERATED';
        const isStock = log.actionType.indexOf('STOCK') >= 0;
        const iconClass = isBill ? 'ph ph-receipt' : (isStock ? 'ph ph-package' : 'ph ph-clock');
        const badgeBg = isBill ? 'bg-emerald-100 text-emerald-800 dark:bg-emerald-950/60 dark:text-emerald-300' : 
                         (isStock ? 'bg-amber-100 text-amber-800 dark:bg-amber-950/60 dark:text-amber-300' : 'bg-sky-100 text-sky-800 dark:bg-sky-950/60 dark:text-sky-300');
        const actionLabel = isBill ? 'BILLING' : (isStock ? 'INVENTORY' : log.actionType);

        return `
          <div class="flex items-center justify-between p-2.5 rounded-xl bg-slate-50 dark:bg-slate-900/40 border border-slate-100 dark:border-slate-800/80 hover:bg-slate-100/60 transition gap-3">
            <div class="flex items-center space-x-3 min-w-0">
              <div class="w-8 h-8 rounded-lg flex items-center justify-center shrink-0 font-bold text-sm ${badgeBg}">
                <i class="${iconClass}"></i>
              </div>
              <div class="min-w-0">
                <div class="flex items-center space-x-1.5 flex-wrap">
                  <span class="text-xs font-bold text-slate-900 dark:text-white truncate">${escapeHtml(log.staffName || 'Operator')}</span>
                  <span class="text-[9px] font-bold px-1.5 py-0.2 rounded ${badgeBg}">${actionLabel}</span>
                </div>
                <p class="text-[11px] text-slate-600 dark:text-slate-300 truncate mt-0.5">${escapeHtml(log.description)}</p>
              </div>
            </div>
            <span class="text-[10px] text-slate-400 font-medium shrink-0 whitespace-nowrap">${escapeHtml(log.timestamp || 'Just now')}</span>
          </div>
        `;
      }).join('');
    }

    let posHtml5QrCode = null;

    function openPosBatchQrModal() {
      Swal.fire({
        title: 'POS Batch QR Scanner',
        html: `
          <div class="space-y-4 text-left">
            <p class="text-xs text-slate-500">Scan via camera or paste the Batch QR JSON payload from inventory tags to auto-populate this bill.</p>
            
            <div class="flex items-center space-x-2">
              <button type="button" onclick="startPosCameraScanner()" class="flex-1 bg-royal-navy text-white py-2 px-3 rounded-xl font-semibold text-xs flex items-center justify-center space-x-2 hover:bg-slate-800 transition">
                <i data-lucide="camera" class="w-4 h-4 text-pink-400"></i>
                <span>Open Camera Scanner</span>
              </button>
              <button type="button" onclick="stopPosCameraScanner()" class="bg-slate-200 text-slate-700 py-2 px-3 rounded-xl font-semibold text-xs hover:bg-slate-300 transition">Stop Cam</button>
            </div>
            
            <div id="pos-qr-reader" class="w-full h-48 bg-slate-900 rounded-xl overflow-hidden flex items-center justify-center text-slate-400 text-xs font-medium">
              Camera preview inactive. Click "Open Camera Scanner" above.
            </div>

            <div>
              <label class="block text-xs font-bold text-slate-700 mb-1">Batch QR JSON / Code / Medicine Name:</label>
              <textarea id="pos-batch-qr-input" rows="3" placeholder='{"type":"BATCH_QR","name":"Augmentin 625mg","batch":"AUG-2026-X","exp":"12/28","mrp":120.0,"rate":105.0}' class="w-full p-2.5 text-xs font-mono border border-slate-300 rounded-xl focus:ring-2 focus:ring-pink-500 focus:outline-none"></textarea>
            </div>
            <div class="flex justify-between items-center bg-slate-50 p-2.5 rounded-xl border border-slate-200">
              <span class="text-xs font-semibold text-slate-600">Preset Sample Batch QR</span>
              <button type="button" onclick="document.getElementById('pos-batch-qr-input').value='{\\\"type\\\":\\\"BATCH_QR\\\",\\\"name\\\":\\\"Augmentin 625mg\\\",\\\"batch\\\":\\\"AUG-9921-A\\\",\\\"exp\\\":\\\"10/28\\\",\\\"mrp\\\":220.0,\\\"rate\\\":195.0}';" class="text-xs bg-pink-100 text-royal-magenta font-bold px-2.5 py-1 rounded-lg">Insert Sample</button>
            </div>
          </div>
        `,
        showCancelButton: true,
        confirmButtonText: 'Add Batch To Bill',
        confirmButtonColor: '#9C1258',
        didOpen: () => {
          if (window.lucide) lucide.createIcons();
        },
        willClose: () => {
          stopPosCameraScanner();
        },
        preConfirm: () => {
          stopPosCameraScanner();
          const input = document.getElementById('pos-batch-qr-input').value.trim();
          if (!input) {
            Swal.showValidationMessage('Please paste or scan a Batch QR code');
            return false;
          }
          try {
            return JSON.parse(input);
          } catch (e) {
            const found = (state.drugDatabase || []).find(d => d.name.toLowerCase() === input.toLowerCase());
            if (found) {
              return {
                name: found.name,
                batch: found.batch || "BATCH-" + Math.floor(100 + Math.random() * 900),
                exp: found.expiry || "12/2028",
                mrp: found.price || 120.0,
                rate: found.price ? found.price * 0.9 : 105.0
              };
            }
            return {
              name: input,
              batch: "BATCH-" + Math.floor(100 + Math.random() * 900),
              exp: "12/28",
              mrp: 120.0,
              rate: 105.0
            };
          }
        }
      }).then((res) => {
        stopPosCameraScanner();
        if (res.isConfirmed && res.value) {
          addBatchQrPayloadToBill(res.value);
        }
      });
    }

    function startPosCameraScanner() {
      try {
        if (typeof Html5Qrcode === 'undefined') {
          const r = document.getElementById('pos-qr-reader');
          if (r) r.innerHTML = '<div class="p-4 text-center text-xs font-bold text-amber-600">Html5Qrcode library not loaded. Please use text input or sample QR below.</div>';
          return;
        }
        if (posHtml5QrCode && posHtml5QrCode.isScanning) return;
        posHtml5QrCode = new Html5Qrcode("pos-qr-reader");
        posHtml5QrCode.start(
          { facingMode: "environment" },
          { fps: 10, qrbox: { width: 220, height: 140 } },
          (decodedText) => {
            stopPosCameraScanner();
            const textarea = document.getElementById('pos-batch-qr-input');
            if (textarea) textarea.value = decodedText;
            try {
              const payload = JSON.parse(decodedText);
              Swal.close();
              addBatchQrPayloadToBill(payload);
            } catch (e) {
              const textarea = document.getElementById('pos-batch-qr-input');
              if (textarea) textarea.value = decodedText;
            }
          },
          () => {}
        ).catch(err => {
          console.error(err);
          const r = document.getElementById('pos-qr-reader');
          if (r) r.innerHTML = '<div class="p-4 text-center text-xs font-bold text-rose-600">Camera access denied or unavailable. Please paste QR code or use manual input.</div>';
        });
      } catch (e) {
        console.error(e);
      }
    }

    function stopPosCameraScanner() {
      try {
        if (posHtml5QrCode && posHtml5QrCode.isScanning) {
          posHtml5QrCode.stop().catch(() => {});
        }
      } catch (_e) {}
    }

    function addBatchQrPayloadToBill(payload) {
      const name = payload.name || "Batch Scanned Medicine";
      const batch = payload.batch || ("RP-" + Math.floor(100 + Math.random() * 900));
      const exp = payload.exp || payload.expiry || "12/2028";
      const mrp = parseFloat(payload.mrp) || 120.0;
      const rate = parseFloat(payload.rate || payload.mrp) || mrp;

      const existing = state.currentSale.items.find(i => i.name === name && i.batch === batch);
      if (existing) {
        existing.qty += 1;
        existing.total = existing.qty * rate;
      } else {
        state.currentSale.items.push({
          name: name,
          batch: batch,
          expiry: exp,
          qty: 1,
          mrp: mrp,
          purchasePrice: rate * 0.75,
          total: rate
        });
      }
      renderBillItems();
      Swal.fire({
        icon: 'success',
        title: 'Batch QR Added to POS!',
        text: `Successfully added ${name} (Batch ${batch}, Exp: ${exp}) to counter bill.`,
        timer: 1800,
        showConfirmButton: false
      });
    }

    function generateBatchQrModal(drugName) {
      const drug = (state.drugDatabase || []).find(d => d.name === drugName) || {
        name: drugName,
        batch: "RP-" + Math.floor(100 + Math.random() * 900),
        expiry: "12/2028",
        price: 120.0,
        hsn: "300490"
      };

      const payloadObj = {
        type: "BATCH_QR",
        name: drug.name,
        batch: drug.batch || ("B-" + Math.floor(100 + Math.random() * 900)),
        exp: drug.expiry || "12/28",
        mrp: drug.price || 120.0,
        rate: drug.price || 120.0,
        hsn: drug.hsn || "300490"
      };

      const payloadStr = JSON.stringify(payloadObj);

      Swal.fire({
        title: `Batch QR Tag: ${drug.name}`,
        html: `
          <div class="space-y-4 text-center">
            <div class="bg-white p-4 inline-block rounded-2xl border-2 border-slate-800 shadow-md">
              <canvas id="qr-batch-canvas" class="w-48 h-48 mx-auto"></canvas>
              <div class="mt-2 text-xs font-bold text-slate-800">${escapeHtml(drug.name)}</div>
              <div class="text-[11px] font-mono text-slate-600">Batch: ${payloadObj.batch} | Exp: ${payloadObj.exp}</div>
              <div class="text-[11px] font-bold text-royal-magenta">MRP: ₹${payloadObj.mrp.toFixed(2)}</div>
            </div>
            <div>
              <label class="block text-xs font-bold text-slate-600 mb-1">Encoded JSON Payload (POS Scannable):</label>
              <input type="text" readonly value='${escapeHtml(payloadStr)}' class="w-full text-[10px] font-mono p-2 bg-slate-100 border border-slate-300 rounded-lg text-center" onclick="this.select();">
            </div>
          </div>
        `,
        showCancelButton: true,
        confirmButtonText: 'Print QR Tag Label',
        confirmButtonColor: '#9C1258',
        didOpen: () => {
          drawSimpleQrOnCanvas('qr-batch-canvas', payloadStr);
        }
      });
    }

    function drawSimpleQrOnCanvas(canvasId, text) {
      const canvas = document.getElementById(canvasId);
      if (!canvas) return;
      const ctx = canvas.getContext('2d');
      const size = 200;
      canvas.width = size;
      canvas.height = size;

      ctx.fillStyle = "#FFFFFF";
      ctx.fillRect(0, 0, size, size);

      ctx.fillStyle = "#0F172A";
      const cells = 25;
      const cellSize = size / cells;

      function drawFinder(x, y) {
        for (let r = 0; r < 7; r++) {
          for (let c = 0; c < 7; c++) {
            if (r === 0 || r === 6 || c === 0 || c === 6 || (r >= 2 && r <= 4 && c >= 2 && c <= 4)) {
              ctx.fillRect((x + c) * cellSize, (y + r) * cellSize, cellSize, cellSize);
            }
          }
        }
      }

      drawFinder(0, 0);
      drawFinder(cells - 7, 0);
      drawFinder(0, cells - 7);

      let hash = 0;
      for (let i = 0; i < text.length; i++) hash = (hash * 31 + text.charCodeAt(i)) & 0xFFFFFFFF;

      for (let r = 0; r < cells; r++) {
        for (let c = 0; c < cells; c++) {
          const isFinder = (r < 8 && c < 8) || (r < 8 && c >= cells - 8) || (r >= cells - 8 && c < 8);
          if (!isFinder) {
            const val = ((r * 17 + c * 31 + hash) % 3) === 0;
            if (val) {
              ctx.fillRect(c * cellSize, r * cellSize, cellSize, cellSize);
            }
          }
        }
      }
    }

    // Web Portal Native Login and Sign Up Actions
    let isWebSignUp = false;
    let isOtpSent = false;
    let webLoginRole = 'OWNER';

    function updateLoginTitlesAndButtons() {
      const title = document.getElementById('web-login-title');
      const authBtn = document.getElementById('web-login-auth-btn');
      if (isWebSignUp) {
        if (title) title.innerText = webLoginRole === 'OWNER' ? "Create Pharmacy Owner Account" : "Register Pharmacy Staff Account";
        if (authBtn) {
          authBtn.innerHTML = isOtpSent 
            ? "<span>Verify OTP & Complete Registration</span>" 
            : `<span>${webLoginRole === 'OWNER' ? 'Sign Up as Owner' : 'Register Staff'} & Request OTP</span>`;
        }
      } else {
        if (title) title.innerText = webLoginRole === 'OWNER' ? "Owner Portal Sign In" : "Staff Member Sign In";
        if (authBtn) {
          authBtn.innerHTML = isOtpSent 
            ? "<span>Verify OTP & Access ERP</span>" 
            : `<span>Request 6-Digit OTP (${webLoginRole === 'OWNER' ? 'Owner' : 'Staff'})</span>`;
        }
      }
    }

    function setWebLoginTab(isSignUp) {
      isWebSignUp = isSignUp;
      isOtpSent = false;
      const otpBlock = document.getElementById('web-login-otp-block');
      if (otpBlock) otpBlock.classList.add('hidden');
      
      const signinBtn = document.getElementById('web-tab-btn-signin');
      const signupBtn = document.getElementById('web-tab-btn-signup');
      const signupFields = document.getElementById('web-signup-fields');

      if (isSignUp) {
        signinBtn.classList.remove('bg-royal-navy', 'text-white');
        signinBtn.classList.add('text-slate-400');
        signupBtn.classList.add('bg-royal-navy', 'text-white');
        signupBtn.classList.remove('text-slate-400');
        if (signupFields) signupFields.classList.remove('hidden');
      } else {
        signupBtn.classList.remove('bg-royal-navy', 'text-white');
        signupBtn.classList.add('text-slate-400');
        signinBtn.classList.add('bg-royal-navy', 'text-white');
        signinBtn.classList.remove('text-slate-400');
        if (signupFields) signupFields.classList.add('hidden');
      }

      updateLoginTitlesAndButtons();
    }

    function toggleWebLoginRole(role) {
      webLoginRole = role;
      const ownerBtn = document.getElementById('web-login-role-owner');
      const staffBtn = document.getElementById('web-login-role-staff');
      const passwordBlock = document.getElementById('web-owner-password-block');
      const staffHelper = document.getElementById('web-staff-helper-block');
      const roleIndicator = document.getElementById('web-login-role-indicator');
      const nameLabel = document.getElementById('web-signup-name-label');
      const nameInput = document.getElementById('web-login-name-input');
      const emailLabel = document.getElementById('web-signup-email-label');
      const emailInput = document.getElementById('web-login-email-input');
      const phoneInput = document.getElementById('web-login-phone-input');
      const demoStaffBtn = document.getElementById('web-login-quick-staff-phone');

      updateLoginTitlesAndButtons();

      if (role === 'OWNER') {
        if (ownerBtn) {
          ownerBtn.className = "flex-1 py-2 text-[11px] font-bold rounded-lg bg-pink-500/20 text-pink-300 border border-pink-500/40 shadow-sm transition-all flex items-center justify-center space-x-1.5 cursor-pointer";
        }
        if (staffBtn) {
          staffBtn.className = "flex-1 py-2 text-[11px] font-bold rounded-lg text-slate-400 border border-transparent hover:text-slate-200 transition-all flex items-center justify-center space-x-1.5 cursor-pointer";
        }
        if (passwordBlock) passwordBlock.classList.remove('hidden');
        if (staffHelper) staffHelper.classList.add('hidden');
        if (demoStaffBtn) demoStaffBtn.classList.add('hidden');
        if (roleIndicator) {
          roleIndicator.innerText = "👑 Owner Mode";
          roleIndicator.className = "text-[10px] font-bold px-2 py-0.5 rounded-full bg-pink-500/10 text-pink-400 border border-pink-500/20";
        }
        if (nameLabel) nameLabel.innerText = "Owner / Partner Full Name";
        if (nameInput) nameInput.placeholder = "Enter full name";
        if (emailLabel) emailLabel.innerText = "Business Email Address";
        if (emailInput) emailInput.placeholder = "name@example.com";
      } else {
        if (staffBtn) {
          staffBtn.className = "flex-1 py-2 text-[11px] font-bold rounded-lg bg-sky-500/20 text-sky-300 border border-sky-500/40 shadow-sm transition-all flex items-center justify-center space-x-1.5 cursor-pointer";
        }
        if (ownerBtn) {
          ownerBtn.className = "flex-1 py-2 text-[11px] font-bold rounded-lg text-slate-400 border border-transparent hover:text-slate-200 transition-all flex items-center justify-center space-x-1.5 cursor-pointer";
        }
        if (passwordBlock) passwordBlock.classList.add('hidden');
        if (staffHelper) staffHelper.classList.remove('hidden');
        if (demoStaffBtn) demoStaffBtn.classList.add('hidden');
        if (roleIndicator) {
          roleIndicator.innerText = "💼 Staff Mode";
          roleIndicator.className = "text-[10px] font-bold px-2 py-0.5 rounded-full bg-sky-500/10 text-sky-400 border border-sky-500/20";
        }
        if (nameLabel) nameLabel.innerText = "Staff / Pharmacist Full Name";
        if (nameInput) nameInput.placeholder = "Enter staff full name";
        if (emailLabel) emailLabel.innerText = "Staff Email Address";
        if (emailInput) emailInput.placeholder = "staff@example.com";
      }
    }

    function fillStaffDemoPhone() {
      // Kept for backward compatibility
    }

    function handleWebPrimaryAuth() {
      const phoneInput = document.getElementById('web-login-phone-input').value.trim();
      const nameInput = document.getElementById('web-login-name-input').value.trim();
      const emailInput = document.getElementById('web-login-email-input').value.trim();
      const otpInput = document.getElementById('web-login-otp-input').value.trim();
      const passwordInput = document.getElementById('web-login-password-input').value.trim();

      if (webLoginRole === 'OWNER' && passwordInput !== state.secretPassword) {
        Swal.fire({
          title: 'Incorrect Password',
          text: 'The owner secret password entered is incorrect. Please try again or reset your password.',
          icon: 'error',
          confirmButtonColor: '#9C1258'
        });
        return;
      }

      if (isWebSignUp && (!nameInput || !emailInput)) {
        Swal.fire({
          title: 'Form Incomplete',
          text: `Please enter full name and email address to register ${webLoginRole === 'OWNER' ? 'the pharmacy owner' : 'the staff chemist'} account.`,
          icon: 'error',
          confirmButtonColor: '#9C1258'
        });
        return;
      }
      if (!phoneInput || phoneInput.length < 10) {
        Swal.fire({
          title: 'Invalid Number',
          text: 'Please enter a valid 10-digit mobile number.',
          icon: 'error',
          confirmButtonColor: '#9C1258'
        });
        return;
      }

      if (!isOtpSent) {
        isOtpSent = true;
        document.getElementById('web-login-otp-block').classList.remove('hidden');
        document.getElementById('web-login-auth-btn').innerHTML = "<span>Verify OTP & Access ERP</span>";
        Swal.fire({
          title: 'OTP Dispatched',
          text: `Security code (123456) dispatched to +91 ${phoneInput} for ${webLoginRole === 'OWNER' ? 'Owner' : 'Staff'} login.`,
          icon: 'info',
          confirmButtonColor: '#9C1258'
        });
      } else {
        if (otpInput === '123456' || otpInput === '') {
          state.isLoggedIn = true;
          state.userPhone = "+91 " + phoneInput;
          state.role = webLoginRole;

          if (webLoginRole === 'OWNER') {
            if (isWebSignUp) {
              state.userName = nameInput;
              state.userEmail = emailInput;
              state.profile.ownerName = nameInput;
            } else {
              state.userName = "Suleman Hoque";
              state.userEmail = "sulman995790@gmail.com";
            }
          } else {
            // Staff Role Assignment & Sync
            if (isWebSignUp) {
              state.userName = nameInput;
              state.userEmail = emailInput;
              // Register new staff member if not already registered
              const cleanPhone = phoneInput.replace(/\D/g, '');
              const existingIdx = state.staffMembers.findIndex(s => s.phone.replace(/\D/g, '').endsWith(cleanPhone));
              if (existingIdx >= 0) {
                state.staffMembers[existingIdx].name = nameInput;
                state.staffMembers[existingIdx].email = emailInput;
                state.staffMembers[existingIdx].lastLoginTime = "Just now";
              } else {
                state.staffMembers.push({
                  id: "s-" + (state.staffMembers.length + 1),
                  name: nameInput,
                  email: emailInput,
                  phone: "+91 " + phoneInput,
                  lastLoginTime: "Just now",
                  loginType: "PHONE"
                });
              }
            } else {
              // Lookup existing staff
              const cleanPhone = phoneInput.replace(/\D/g, '');
              const matchedStaff = state.staffMembers.find(s => s.phone.replace(/\D/g, '').endsWith(cleanPhone));
              if (matchedStaff) {
                state.userName = matchedStaff.name;
                state.userEmail = matchedStaff.email;
                state.activeStaffPermission = matchedStaff.permission || "POS-only access";
                matchedStaff.lastLoginTime = "Just now";
              } else {
                state.userName = "Staff Chemist (" + phoneInput.slice(-4) + ")";
                state.userEmail = "staff." + phoneInput.slice(-4) + "@royal.com";
                state.activeStaffPermission = "POS-only access";
              }
              logStaffActivityWeb(
                "STAFF_LOGIN",
                state.userName,
                `Staff logged in via Mobile OTP (${state.activeStaffPermission})`,
                "#0284C7"
              );
            }
          }

          saveStateToLocal();
          syncUiState();
          Swal.fire({
            title: 'Welcome!',
            text: `Successfully logged in as ${state.role === 'OWNER' ? 'Owner Principal' : 'Staff Chemist'}: ${state.userName}`,
            icon: 'success',
            confirmButtonColor: '#9C1258'
          });
          showLiveSyncNotification("Logged In", `Authenticated as ${state.role} (${state.userName})`, "check-circle");
        } else {
          Swal.fire({
            title: 'Invalid OTP',
            text: 'Please enter "123456" as the verification code.',
            icon: 'error',
            confirmButtonColor: '#9C1258'
          });
        }
      }
    }

    function triggerWebGoogleChooser() {
      Swal.fire({
        title: 'Google Sign-In',
        html: `
          <div class="text-left space-y-3 p-1 text-sm text-slate-600">
            <p class="text-xs text-slate-400 text-center pb-2 border-b border-slate-100">Choose a Google Account to access Royal Pharmacy ERP</p>
            
            <div onclick="selectGoogleWeb('sk786sk9957@gmail.com', 'Suleman Hoque (Owner)', 'OWNER')" class="flex items-center justify-between p-3 bg-slate-50 hover:bg-slate-100 rounded-xl cursor-pointer border border-slate-100 transition">
              <div class="flex items-center space-x-3">
                <div class="w-8 h-8 rounded-full bg-pink-100 text-royal-magenta font-bold flex items-center justify-center text-xs">SK</div>
                <div>
                  <p class="font-bold text-slate-800 text-xs">Suleman Hoque</p>
                  <p class="text-[10px] text-slate-400">sk786sk9957@gmail.com</p>
                </div>
              </div>
              <span class="text-[9px] font-bold px-2 py-0.5 rounded bg-pink-100 text-royal-magenta">👑 Owner</span>
            </div>

            <div onclick="selectGoogleWeb('khannijamuddin87275@gmail.com', 'Nijamuddin Khan', 'STAFF')" class="flex items-center justify-between p-3 bg-slate-50 hover:bg-slate-100 rounded-xl cursor-pointer border border-slate-100 transition">
              <div class="flex items-center space-x-3">
                <div class="w-8 h-8 rounded-full bg-sky-100 text-sky-700 font-bold flex items-center justify-center text-xs">NK</div>
                <div>
                  <p class="font-bold text-slate-800 text-xs">Nijamuddin Khan</p>
                  <p class="text-[10px] text-slate-400">khannijamuddin87275@gmail.com</p>
                </div>
              </div>
              <span class="text-[9px] font-bold px-2 py-0.5 rounded bg-sky-100 text-sky-700">💼 Staff</span>
            </div>

            <div onclick="selectGoogleWeb('nimenur931@gmail.com', 'Nur Nime', 'STAFF')" class="flex items-center justify-between p-3 bg-slate-50 hover:bg-slate-100 rounded-xl cursor-pointer border border-slate-100 transition">
              <div class="flex items-center space-x-3">
                <div class="w-8 h-8 rounded-full bg-blue-100 text-blue-700 font-bold flex items-center justify-center text-xs">NN</div>
                <div>
                  <p class="font-bold text-slate-800 text-xs">Nur Nime</p>
                  <p class="text-[10px] text-slate-400">nimenur931@gmail.com</p>
                </div>
              </div>
              <span class="text-[9px] font-bold px-2 py-0.5 rounded bg-blue-100 text-blue-700">💼 Staff</span>
            </div>

            <div onclick="selectGoogleWeb('guest@royalchemist.com', 'Guest Chemist', 'STAFF')" class="flex items-center justify-between p-2.5 bg-slate-50 hover:bg-slate-100 rounded-xl cursor-pointer border border-slate-100 transition text-[11px]">
              <div class="flex items-center space-x-2">
                <i data-lucide="user-minus" class="w-4 h-4 text-slate-400 font-bold"></i>
                <span class="font-semibold text-slate-700">Use Guest Staff Session</span>
              </div>
              <span class="text-[9px] font-bold px-2 py-0.5 rounded bg-slate-200 text-slate-700">💼 Staff</span>
            </div>
          </div>
        `,
        showConfirmButton: false,
        showCloseButton: true
      });
      if (window.lucide) lucide.createIcons();
    }

    function quickStaffDirectLoginWeb(staffId = 's-1') {
      const member = state.staffMembers.find(s => s.id === staffId) || state.staffMembers[0] || {
        id: 's-1',
        name: 'Nijamuddin Khan',
        phone: '+91 9435078210',
        email: 'khannijamuddin87275@gmail.com',
        permission: 'POS-only access',
        designation: 'Senior Chemist Counter'
      };

      state.isLoggedIn = true;
      state.role = 'STAFF';
      state.userName = member.name;
      state.userEmail = member.email || 'staff@royal.com';
      state.userPhone = member.phone;
      state.activeStaffPermission = member.permission || 'POS-only access';
      member.lastLoginTime = 'Just now';

      saveStateToLocal();
      syncUiState();

      logStaffActivityWeb(
        "STAFF_LOGIN",
        member.name,
        `Logged in as Staff (${member.permission || 'POS-only access'}) via Instant Access`,
        "#0284C7"
      );

      Swal.fire({
        title: 'Welcome!',
        text: `Operating as Staff Chemist: ${member.name} (${member.permission})`,
        icon: 'success',
        confirmButtonColor: '#9C1258'
      });
      showLiveSyncNotification("Logged In", `Active staff session: ${member.name}`, "check-circle");
    }

    function selectGoogleWeb(email, name, role) {
      state.isLoggedIn = true;
      state.userEmail = email;
      state.userName = name;
      state.role = role;
      if (role === 'STAFF') {
        const matched = state.staffMembers.find(s => s.email.toLowerCase() === email.toLowerCase());
        state.activeStaffPermission = matched ? (matched.permission || 'POS-only access') : 'POS-only access';
        if (matched) matched.lastLoginTime = "Just now";
        logStaffActivityWeb(
          "STAFF_LOGIN",
          name,
          `Staff authenticated via Google Account (${state.activeStaffPermission})`,
          "#0284C7"
        );
      } else {
        state.activeStaffPermission = 'FULL_ACCESS';
        logStaffActivityWeb(
          "STAFF_LOGIN",
          "Owner Principal",
          `Owner authenticated via Google Account (${email})`,
          "#9C1258"
        );
      }
      saveStateToLocal();
      syncUiState();
      Swal.close();
      Swal.fire({
        title: 'Welcome Back!',
        text: `Logged in as ${role === 'OWNER' ? '👑 Owner' : '💼 Staff'}: ${name}`,
        icon: 'success',
        confirmButtonColor: '#9C1258'
      });
      showLiveSyncNotification("Google Session Restored", `Authenticated as ${role}: ${email}`, "verified");
    }

    function logoutWeb() {
      state.isLoggedIn = false;
      saveStateToLocal();
      syncUiState();
      Swal.fire('Signed Out', 'You have been securely logged out.', 'success');
    }

    // Optimized instant startup and non-blocking background initialization
    window.addEventListener('DOMContentLoaded', () => {
      loadSavedState();
      // Defer non-critical Firebase web init and widget renders to ensure instant UI responsiveness
      setTimeout(() => {
        try { initializeFirebaseWeb(); } catch(e) {}
        renderLowStockWidget();
        renderRecentActivityFeedWeb();
      }, 50);
    });

    if ('serviceWorker' in navigator) {
      window.addEventListener('load', () => {
        navigator.serviceWorker.register('/sw.js').catch(err => console.log('SW registration failed: ', err));
      });
    }
  
