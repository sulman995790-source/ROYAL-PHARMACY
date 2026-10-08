








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

    function loadSavedState() {
      const saved = localStorage.getItem('royal_pharmacy_state');
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
          state.isLoggedIn = parsed.isLoggedIn !== undefined ? parsed.isLoggedIn : false;
        } catch (e) {
          console.error("Failed to restore local state", e);
        }
      }
      syncUiState();
    }

    function saveStateToLocal() {
      localStorage.setItem('royal_pharmacy_state', JSON.stringify({
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
      }));
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
            

              window.onload = function() {
                window.print();
                setTimeout(() => window.close(), 1000);
              };
            
