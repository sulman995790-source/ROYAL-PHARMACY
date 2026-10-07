package com.example.data.ai

import com.example.data.model.BrandMedicine

object BrandCatalogProvider {
  val POPULAR_BRANDS = listOf(
    BrandInfo("Cipla", "Global leader in respiratory, anti-infectives, and critical care injectables", 0xFFE3F2FD),
    BrandInfo("SUN PHARMA", "India's #1 pharmaceutical powerhouse in cardiology, neurology & injectables", 0xFFFFF3E0),
    BrandInfo("Mankind", "Affordable quality medicines, antibiotics, and vitamins across India", 0xFFE8F5E9),
    BrandInfo("ALKEM", "Leading player in anti-infectives, gastro, and high-potency hospital injectables", 0xFFEDE7F6),
    BrandInfo("IPCA", "Global supplier of pain management, antimalarials, and cardiovascular active salts", 0xFFE0F2F1),
    BrandInfo("GSK", "World-renowned research pharma behind Augmentin, Calpol, and Betnovate", 0xFFFFF8E1),
    BrandInfo("Abbott", "Pioneers in nutrition, thyroid, women's health, and metabolic wellness", 0xFFE0F7FA),
    BrandInfo("INTAS", "Rapidly growing biosimilars, oncology, and specialty hospital care medications", 0xFFFCE4EC),
    BrandInfo("LUPIN", "Global giant in cardiology, diabetes, respiratory, and anti-TB therapy", 0xFFF1F8E9),
    BrandInfo("torrent", "Specialists in cardiovascular, CNS, gastrointestinal, and pain management", 0xFFFFF8E1),
    BrandInfo("Dr. Reddy's", "Innovator in gastroenterology, pain, oncology, and generic active substances", 0xFFF3E5F5),
    BrandInfo("MACLEODS", "Top anti-tuberculosis, antibacterial, and respiratory formulation manufacturer", 0xFFF9FBE7),
    BrandInfo("ARISTO", "Makers of India's trusted Monocef injections, Megapen, and pediatric enzymes", 0xFFFFEBEE),
    BrandInfo("Zydus", "Pioneers in Dexona, Deriphyllin, vaccines, and critical care hospital injectables", 0xFFE8EAF6),
    BrandInfo("Glenmark", "Leaders in dermatology, respiratory (Ascoril), and novel therapeutics", 0xFFEFEBE9),
    BrandInfo("Leeford", "Rapidly expanding retail healthcare, derma, OTC, and generic formulations", 0xFFEDE7F6)
  )

  data class BrandInfo(
    val name: String,
    val description: String,
    val colorHex: Long
  )

  fun getBrandMedicines(brand: String): List<BrandMedicine> {
    val b = brand.trim().uppercase()
    return when {
      b.contains("CIPLA") -> ciplaMedicines
      b.contains("IPCA") -> ipcaMedicines
      b.contains("GSK") || b.contains("GLAXO") -> gskMedicines
      b.contains("SUN") -> sunPharmaMedicines
      b.contains("ALKEM") -> alkemMedicines
      b.contains("MANKIND") -> mankindMedicines
      b.contains("ABBOTT") -> abbottMedicines
      b.contains("INTAS") -> intasMedicines
      b.contains("LUPIN") -> lupinMedicines
      b.contains("TORRENT") -> torrentMedicines
      b.contains("REDDY") -> drReddysMedicines
      b.contains("MACLEODS") -> macleodsMedicines
      b.contains("ARISTO") -> aristoMedicines
      b.contains("ZYDUS") -> zydusMedicines
      b.contains("GLENMARK") -> glenmarkMedicines
      b.contains("LEEFORD") -> leefordMedicines
      else -> genericBrandFallback(brand)
    }
  }

  // --- BRAND SPECIFIC CATALOGS (Including Injectables, Tablets, Syrups) ---
  private val ciplaMedicines = listOf(
    BrandMedicine(name = "Asthalin 100mcg Inhaler", brandName = "Cipla", saltComposition = "Salbutamol 100mcg", category = "Inhaler", mrp = 168.0, packaging = "200 metered doses"),
    BrandMedicine(name = "Ciplox 500mg Tablet", brandName = "Cipla", saltComposition = "Ciprofloxacin 500mg", category = "Tablet", mrp = 48.5, packaging = "10 Tablets"),
    BrandMedicine(name = "Azee 500mg Tablet", brandName = "Cipla", saltComposition = "Azithromycin 500mg", category = "Tablet", mrp = 125.0, packaging = "5 Tablets"),
    BrandMedicine(name = "Foracort 200 Inhaler", brandName = "Cipla", saltComposition = "Budesonide 200mcg + Formoterol 6mcg", category = "Inhaler", mrp = 385.0, packaging = "120 metered doses"),
    BrandMedicine(name = "Emeset 2ml Injection", brandName = "Cipla", saltComposition = "Ondansetron 2mg/ml", category = "Injectable", mrp = 34.0, packaging = "2ml Ampoule", isInjectable = true, isEssential = true, description = "Essential antiemetic IV/IM injection for chemotherapy and post-op nausea"),
    BrandMedicine(name = "Pansec 40mg Injection", brandName = "Cipla", saltComposition = "Pantoprazole 40mg IV", category = "Injectable", mrp = 56.0, packaging = "1 Vial with sterile water", isInjectable = true, isEssential = true, description = "Proton pump inhibitor IV injection for acute peptic ulcers and GERD"),
    BrandMedicine(name = "Ciplacef 1g Injection", brandName = "Cipla", saltComposition = "Ceftriaxone 1000mg", category = "Injectable", mrp = 68.0, packaging = "1 Vial IV/IM", isInjectable = true, isEssential = true, description = "Broad-spectrum cephalosporin antibiotic injection"),
    BrandMedicine(name = "Cipladine 5% Ointment", brandName = "Cipla", saltComposition = "Povidone Iodine 5% w/w", category = "Ointment", mrp = 55.0, packaging = "20g Tube"),
    BrandMedicine(name = "Budecort 0.5mg Respules", brandName = "Cipla", saltComposition = "Budesonide 0.5mg/2ml", category = "Syrup", mrp = 145.0, packaging = "5 Respules of 2ml"),
    BrandMedicine(name = "Doxicip 100mg Capsule", brandName = "Cipla", saltComposition = "Doxycycline 100mg", category = "Capsule", mrp = 38.0, packaging = "8 Capsules")
  )

  private val ipcaMedicines = listOf(
    BrandMedicine(name = "Zerodol-P Tablet", brandName = "IPCA", saltComposition = "Aceclofenac 100mg + Paracetamol 325mg", category = "Tablet", mrp = 68.0, packaging = "10 Tablets", description = "Leading analgesic and anti-inflammatory combination"),
    BrandMedicine(name = "Zerodol-SP Tablet", brandName = "IPCA", saltComposition = "Aceclofenac 100mg + Paracetamol 325mg + Serratiopeptidase 15mg", category = "Tablet", mrp = 120.0, packaging = "10 Tablets"),
    BrandMedicine(name = "Pacimol 650mg Tablet", brandName = "IPCA", saltComposition = "Paracetamol 650mg", category = "Tablet", mrp = 24.5, packaging = "15 Tablets", isEssential = true),
    BrandMedicine(name = "Pacimol 100ml IV Infusion", brandName = "IPCA", saltComposition = "Paracetamol 1000mg/100ml", category = "Injectable", mrp = 82.0, packaging = "100ml Bottle", isInjectable = true, isEssential = true, description = "Intravenous paracetamol infusion for rapid post-operative pain and pyrexia"),
    BrandMedicine(name = "Perinorm 2ml Injection", brandName = "IPCA", saltComposition = "Metoclopramide 5mg/ml", category = "Injectable", mrp = 18.0, packaging = "2ml Ampoule", isInjectable = true, isEssential = true, description = "Prokinetic antiemetic injection for gastrointestinal motility"),
    BrandMedicine(name = "Lariago 40ml Injection", brandName = "IPCA", saltComposition = "Chloroquine Phosphate 64.5mg/ml", category = "Injectable", mrp = 45.0, packaging = "30ml Multi-dose Vial", isInjectable = true, isEssential = true),
    BrandMedicine(name = "HCQS 200mg Tablet", brandName = "IPCA", saltComposition = "Hydroxychloroquine 200mg", category = "Tablet", mrp = 118.0, packaging = "10 Tablets"),
    BrandMedicine(name = "Folvite 5mg Tablet", brandName = "IPCA", saltComposition = "Folic Acid 5mg", category = "Tablet", mrp = 38.0, packaging = "45 Tablets", isEssential = true),
    BrandMedicine(name = "Rapither 150mg Injection", brandName = "IPCA", saltComposition = "Arteether 150mg/2ml", category = "Injectable", mrp = 135.0, packaging = "2ml Ampoule IM", isInjectable = true, isEssential = true, description = "Life-saving antimalarial injection for severe falciparum malaria"),
    BrandMedicine(name = "Tenoretic 50mg Tablet", brandName = "IPCA", saltComposition = "Atenolol 50mg + Chlorthalidone 12.5mg", category = "Tablet", mrp = 85.0, packaging = "14 Tablets")
  )

  private val gskMedicines = listOf(
    BrandMedicine(name = "Augmentin 625 Duo Tablet", brandName = "GSK", saltComposition = "Amoxicillin 500mg + Clavulanic Acid 125mg", category = "Tablet", mrp = 210.0, packaging = "10 Tablets", isEssential = true, description = "Gold standard co-amoxiclav antibiotic"),
    BrandMedicine(name = "Calpol 650mg Tablet", brandName = "GSK", saltComposition = "Paracetamol 650mg", category = "Tablet", mrp = 32.0, packaging = "15 Tablets", isEssential = true),
    BrandMedicine(name = "Calpol 250mg Suspension", brandName = "GSK", saltComposition = "Paracetamol 250mg/5ml", category = "Syrup", mrp = 54.0, packaging = "60ml Bottle"),
    BrandMedicine(name = "Augmentin 1.2g IV Injection", brandName = "GSK", saltComposition = "Amoxicillin 1000mg + Clavulanate 200mg", category = "Injectable", mrp = 145.0, packaging = "1 Vial with sterile water", isInjectable = true, isEssential = true, description = "Intravenous co-amoxiclav for severe respiratory & intra-abdominal infections"),
    BrandMedicine(name = "Zinacef 750mg Injection", brandName = "GSK", saltComposition = "Cefuroxime 750mg", category = "Injectable", mrp = 95.0, packaging = "1 Vial IV/IM", isInjectable = true, isEssential = true),
    BrandMedicine(name = "Betnovate-C Cream", brandName = "GSK", saltComposition = "Betamethasone Valerate 0.1% + Clioquinol 3%", category = "Ointment", mrp = 62.0, packaging = "30g Tube"),
    BrandMedicine(name = "Betnovate-N Cream", brandName = "GSK", saltComposition = "Betamethasone Valerate 0.1% + Neomycin 0.5%", category = "Ointment", mrp = 58.0, packaging = "20g Tube"),
    BrandMedicine(name = "Zentel 400mg Tablet", brandName = "GSK", saltComposition = "Albendazole 400mg", category = "Tablet", mrp = 9.8, packaging = "1 Tablet chewable", isEssential = true),
    BrandMedicine(name = "Ceftum 500mg Tablet", brandName = "GSK", saltComposition = "Cefuroxime Axetil 500mg", category = "Tablet", mrp = 485.0, packaging = "10 Tablets"),
    BrandMedicine(name = "Piriton Expectorant", brandName = "GSK", saltComposition = "Chlorpheniramine + Ammonium Chloride", category = "Syrup", mrp = 88.0, packaging = "100ml Bottle")
  )

  private val sunPharmaMedicines = listOf(
    BrandMedicine(name = "Pantocid 40mg Tablet", brandName = "SUN PHARMA", saltComposition = "Pantoprazole 40mg", category = "Tablet", mrp = 165.0, packaging = "15 Tablets"),
    BrandMedicine(name = "Pantocid 40mg IV Injection", brandName = "SUN PHARMA", saltComposition = "Pantoprazole 40mg IV", category = "Injectable", mrp = 62.0, packaging = "1 Vial with solvent", isInjectable = true, isEssential = true),
    BrandMedicine(name = "Volini Pain Relief Gel", brandName = "SUN PHARMA", saltComposition = "Diclofenac Diethylamine + Methyl Salicylate", category = "Ointment", mrp = 125.0, packaging = "30g Tube"),
    BrandMedicine(name = "Rosuvas 10mg Tablet", brandName = "SUN PHARMA", saltComposition = "Rosuvastatin 10mg", category = "Tablet", mrp = 230.0, packaging = "15 Tablets"),
    BrandMedicine(name = "Meromer 1g Injection", brandName = "SUN PHARMA", saltComposition = "Meropenem 1000mg", category = "Injectable", mrp = 750.0, packaging = "1 Vial IV", isInjectable = true, isEssential = true, description = "Critical hospital carbapenem antibiotic for resistant ICU infections"),
    BrandMedicine(name = "Gemer 1 Tablet", brandName = "SUN PHARMA", saltComposition = "Glimepiride 1mg + Metformin 500mg", category = "Tablet", mrp = 88.0, packaging = "10 Tablets"),
    BrandMedicine(name = "Susten 200mg Capsule", brandName = "SUN PHARMA", saltComposition = "Natural Micronised Progesterone 200mg", category = "Capsule", mrp = 390.0, packaging = "10 Capsules"),
    BrandMedicine(name = "Susten 100mg Injection", brandName = "SUN PHARMA", saltComposition = "Progesterone 100mg/ml", category = "Injectable", mrp = 185.0, packaging = "1 Ampoule IM", isInjectable = true),
    BrandMedicine(name = "Storvas 10mg Tablet", brandName = "SUN PHARMA", saltComposition = "Atorvastatin 10mg", category = "Tablet", mrp = 145.0, packaging = "15 Tablets")
  )

  private val alkemMedicines = listOf(
    BrandMedicine(name = "Pan 40mg Tablet", brandName = "ALKEM", saltComposition = "Pantoprazole 40mg", category = "Tablet", mrp = 158.0, packaging = "15 Tablets"),
    BrandMedicine(name = "Pan-D Capsule", brandName = "ALKEM", saltComposition = "Pantoprazole 40mg + Domperidone 30mg SR", category = "Capsule", mrp = 210.0, packaging = "15 Capsules"),
    BrandMedicine(name = "Clavam 625mg Tablet", brandName = "ALKEM", saltComposition = "Amoxicillin 500mg + Clavulanic Acid 125mg", category = "Tablet", mrp = 205.0, packaging = "10 Tablets", isEssential = true),
    BrandMedicine(name = "Clavam 1.2g IV Injection", brandName = "ALKEM", saltComposition = "Amoxicillin 1000mg + Clavulanate 200mg", category = "Injectable", mrp = 138.0, packaging = "1 Vial", isInjectable = true, isEssential = true),
    BrandMedicine(name = "Taxim 1g Injection", brandName = "ALKEM", saltComposition = "Cefotaxime Sodium 1000mg", category = "Injectable", mrp = 48.0, packaging = "1 Vial IV/IM", isInjectable = true, isEssential = true, description = "Essential hospital 3rd generation cephalosporin injection"),
    BrandMedicine(name = "Pipzo 4.5g Injection", brandName = "ALKEM", saltComposition = "Piperacillin 4000mg + Tazobactam 500mg", category = "Injectable", mrp = 425.0, packaging = "1 Vial IV", isInjectable = true, isEssential = true, description = "Broad-spectrum antipseudomonal ICU injectable"),
    BrandMedicine(name = "Taxim-O 200mg Tablet", brandName = "ALKEM", saltComposition = "Cefixime 200mg", category = "Tablet", mrp = 115.0, packaging = "10 Tablets"),
    BrandMedicine(name = "A to Z NS Tablet", brandName = "ALKEM", saltComposition = "Multivitamins + Minerals + Zinc", category = "Tablet", mrp = 135.0, packaging = "15 Tablets"),
    BrandMedicine(name = "Ondem 2ml Injection", brandName = "ALKEM", saltComposition = "Ondansetron 2mg/ml", category = "Injectable", mrp = 28.0, packaging = "2ml Ampoule", isInjectable = true, isEssential = true)
  )

  private val mankindMedicines = listOf(
    BrandMedicine(name = "Moxikind-CV 625 Tablet", brandName = "Mankind", saltComposition = "Amoxicillin 500mg + Potassium Clavulanate 125mg", category = "Tablet", mrp = 175.0, packaging = "10 Tablets", isEssential = true),
    BrandMedicine(name = "Gudcef 200mg Tablet", brandName = "Mankind", saltComposition = "Cefpodoxime Proxetil 200mg", category = "Tablet", mrp = 185.0, packaging = "10 Tablets"),
    BrandMedicine(name = "Mahacef 1g Injection", brandName = "Mankind", saltComposition = "Ceftriaxone 1000mg", category = "Injectable", mrp = 58.0, packaging = "1 Vial with sterile water", isInjectable = true, isEssential = true),
    BrandMedicine(name = "Nurokind-Plus RF Injection", brandName = "Mankind", saltComposition = "Mecobalamin 1000mcg + Pyridoxine + Nicotinamide", category = "Injectable", mrp = 85.0, packaging = "2ml Dispo Pack", isInjectable = true),
    BrandMedicine(name = "Mikacin 500mg Injection", brandName = "Mankind", saltComposition = "Amikacin Sulphate 500mg/2ml", category = "Injectable", mrp = 75.0, packaging = "2ml Vial", isInjectable = true, isEssential = true, description = "Essential aminoglycoside antibiotic injection for severe gram-negative sepsis"),
    BrandMedicine(name = "Telmikind 40mg Tablet", brandName = "Mankind", saltComposition = "Telmisartan 40mg", category = "Tablet", mrp = 42.0, packaging = "10 Tablets", isEssential = true),
    BrandMedicine(name = "Gas-O-Fast Sachet", brandName = "Mankind", saltComposition = "Ayurvedic antacid with Jeera", category = "Syrup", mrp = 10.0, packaging = "5g Sachet")
  )

  private val aristoMedicines = listOf(
    BrandMedicine(name = "Monocef 1g Injection", brandName = "ARISTO", saltComposition = "Ceftriaxone 1000mg IV/IM", category = "Injectable", mrp = 62.0, packaging = "1 Vial with WFI", isInjectable = true, isEssential = true, description = "India's #1 prescribed ceftriaxone injection brand"),
    BrandMedicine(name = "Monocef-SB 1.5g Injection", brandName = "ARISTO", saltComposition = "Ceftriaxone 1000mg + Sulbactam 500mg", category = "Injectable", mrp = 185.0, packaging = "1 Vial", isInjectable = true, isEssential = true),
    BrandMedicine(name = "Monocef-O 200mg Tablet", brandName = "ARISTO", saltComposition = "Cefpodoxime 200mg", category = "Tablet", mrp = 180.0, packaging = "10 Tablets"),
    BrandMedicine(name = "Megapen 500mg Injection", brandName = "ARISTO", saltComposition = "Ampicillin 250mg + Cloxacillin 250mg", category = "Injectable", mrp = 22.0, packaging = "1 Vial IV/IM", isInjectable = true, isEssential = true),
    BrandMedicine(name = "Aristozyme Liquid", brandName = "ARISTO", saltComposition = "Diastase + Pepsin Digestive Enzyme", category = "Syrup", mrp = 125.0, packaging = "200ml Bottle"),
    BrandMedicine(name = "Omnacortil 20mg Tablet", brandName = "ARISTO", saltComposition = "Prednisolone 20mg", category = "Tablet", mrp = 48.0, packaging = "10 Tablets", isEssential = true)
  )

  private val zydusMedicines = listOf(
    BrandMedicine(name = "Dexona 2ml Injection", brandName = "Zydus", saltComposition = "Dexamethasone Sodium Phosphate 4mg/ml", category = "Injectable", mrp = 12.5, packaging = "2ml Vial", isInjectable = true, isEssential = true, description = "Life-saving corticosteroid injection for anaphylaxis, shock, and severe inflammation"),
    BrandMedicine(name = "Deriphyllin 2ml Injection", brandName = "Zydus", saltComposition = "Etofylline 84.7mg + Theophylline 25.3mg/ml", category = "Injectable", mrp = 14.0, packaging = "2ml Ampoule", isInjectable = true, isEssential = true, description = "Essential bronchodilator injection for acute bronchial asthma and COPD exacerbations"),
    BrandMedicine(name = "Deriphyllin Retard 150 Tablet", brandName = "Zydus", saltComposition = "Theophylline + Etofylline Prolonged Release", category = "Tablet", mrp = 38.0, packaging = "30 Tablets"),
    BrandMedicine(name = "Aten 50mg Tablet", brandName = "Zydus", saltComposition = "Atenolol 50mg", category = "Tablet", mrp = 75.0, packaging = "14 Tablets", isEssential = true),
    BrandMedicine(name = "Zydol 100mg Injection", brandName = "Zydus", saltComposition = "Tramadol Hydrochloride 50mg/ml", category = "Injectable", mrp = 28.0, packaging = "2ml Ampoule", isInjectable = true, isEssential = true)
  )

  private val abbottMedicines = listOf(
    BrandMedicine(name = "Thyronorm 100mcg Tablet", brandName = "Abbott", saltComposition = "Levothyroxine Sodium 100mcg", category = "Tablet", mrp = 185.0, packaging = "120 Tablets", isEssential = true),
    BrandMedicine(name = "Duphaston 10mg Tablet", brandName = "Abbott", saltComposition = "Dydrogesterone 10mg", category = "Tablet", mrp = 790.0, packaging = "10 Tablets"),
    BrandMedicine(name = "Digene Antacid Gel", brandName = "Abbott", saltComposition = "Magnesium Hydroxide + Aluminium Hydroxide + Simethicone", category = "Syrup", mrp = 140.0, packaging = "200ml Bottle"),
    BrandMedicine(name = "Brufen 400mg Tablet", brandName = "Abbott", saltComposition = "Ibuprofen 400mg", category = "Tablet", mrp = 18.0, packaging = "15 Tablets", isEssential = true),
    BrandMedicine(name = "Vertin 16mg Tablet", brandName = "Abbott", saltComposition = "Betahistine 16mg", category = "Tablet", mrp = 240.0, packaging = "15 Tablets"),
    BrandMedicine(name = "Klacid IV 500mg Injection", brandName = "Abbott", saltComposition = "Clarithromycin 500mg IV", category = "Injectable", mrp = 680.0, packaging = "1 Vial", isInjectable = true)
  )

  private val intasMedicines = listOf(
    BrandMedicine(name = "Intatax 1g Injection", brandName = "INTAS", saltComposition = "Ceftriaxone 1000mg", category = "Injectable", mrp = 59.0, packaging = "1 Vial", isInjectable = true, isEssential = true),
    BrandMedicine(name = "Meromac 1g Injection", brandName = "INTAS", saltComposition = "Meropenem 1000mg", category = "Injectable", mrp = 720.0, packaging = "1 Vial IV", isInjectable = true, isEssential = true),
    BrandMedicine(name = "Gabapin-NT Tablet", brandName = "INTAS", saltComposition = "Gabapentin 400mg + Nortriptyline 10mg", category = "Tablet", mrp = 245.0, packaging = "10 Tablets"),
    BrandMedicine(name = "Intagesic-MR Tablet", brandName = "INTAS", saltComposition = "Diclofenac + Paracetamol + Chlorzoxazone", category = "Tablet", mrp = 110.0, packaging = "10 Tablets"),
    BrandMedicine(name = "Telma 40mg Tablet", brandName = "INTAS", saltComposition = "Telmisartan 40mg", category = "Tablet", mrp = 115.0, packaging = "15 Tablets", isEssential = true)
  )

  private val lupinMedicines = listOf(
    BrandMedicine(name = "Rablet 20mg Tablet", brandName = "LUPIN", saltComposition = "Rabeprazole 20mg", category = "Tablet", mrp = 125.0, packaging = "15 Tablets"),
    BrandMedicine(name = "Tonact 10mg Tablet", brandName = "LUPIN", saltComposition = "Atorvastatin 10mg", category = "Tablet", mrp = 135.0, packaging = "15 Tablets"),
    BrandMedicine(name = "Gluconorm-G 1 Tablet", brandName = "LUPIN", saltComposition = "Glimepiride 1mg + Metformin 500mg", category = "Tablet", mrp = 82.0, packaging = "10 Tablets"),
    BrandMedicine(name = "Lupisulin M30 Injection", brandName = "LUPIN", saltComposition = "Biphasic Isophane Insulin 30/70", category = "Injectable", mrp = 180.0, packaging = "10ml Vial", isInjectable = true, isEssential = true)
  )

  private val torrentMedicines = listOf(
    BrandMedicine(name = "Chymoral Forte Tablet", brandName = "torrent", saltComposition = "Trypsin-Chymotrypsin 100000 AU", category = "Tablet", mrp = 425.0, packaging = "20 Tablets"),
    BrandMedicine(name = "Shelcal 500mg Tablet", brandName = "torrent", saltComposition = "Calcium 500mg + Vitamin D3 250 IU", category = "Tablet", mrp = 135.0, packaging = "15 Tablets", isEssential = true),
    BrandMedicine(name = "Nexpro 40mg Tablet", brandName = "torrent", saltComposition = "Esomeprazole 40mg", category = "Tablet", mrp = 145.0, packaging = "15 Tablets"),
    BrandMedicine(name = "Dynapar AQ 75mg Injection", brandName = "torrent", saltComposition = "Diclofenac Sodium 75mg/ml (Aqueous)", category = "Injectable", mrp = 32.0, packaging = "1ml Ampoule IV/IM", isInjectable = true, isEssential = true)
  )

  private val drReddysMedicines = listOf(
    BrandMedicine(name = "Omez 20mg Capsule", brandName = "Dr. Reddy's", saltComposition = "Omeprazole 20mg", category = "Capsule", mrp = 75.0, packaging = "20 Capsules", isEssential = true),
    BrandMedicine(name = "Omez 40mg IV Injection", brandName = "Dr. Reddy's", saltComposition = "Omeprazole 40mg IV", category = "Injectable", mrp = 48.0, packaging = "1 Vial with solvent", isInjectable = true, isEssential = true),
    BrandMedicine(name = "Ketorol-DT Tablet", brandName = "Dr. Reddy's", saltComposition = "Ketorolac Tromethamine 10mg", category = "Tablet", mrp = 135.0, packaging = "15 Tablets"),
    BrandMedicine(name = "Ketorol 30mg/ml Injection", brandName = "Dr. Reddy's", saltComposition = "Ketorolac Tromethamine 30mg/ml", category = "Injectable", mrp = 28.0, packaging = "1ml Ampoule IM/IV", isInjectable = true, isEssential = true),
    BrandMedicine(name = "Razo 20mg Tablet", brandName = "Dr. Reddy's", saltComposition = "Rabeprazole 20mg", category = "Tablet", mrp = 160.0, packaging = "15 Tablets")
  )

  private val macleodsMedicines = listOf(
    BrandMedicine(name = "Macfast 650mg Tablet", brandName = "MACLEODS", saltComposition = "Paracetamol 650mg", category = "Tablet", mrp = 28.0, packaging = "15 Tablets", isEssential = true),
    BrandMedicine(name = "Macpod 200mg Tablet", brandName = "MACLEODS", saltComposition = "Cefpodoxime 200mg", category = "Tablet", mrp = 175.0, packaging = "10 Tablets"),
    BrandMedicine(name = "Maczone 1g Injection", brandName = "MACLEODS", saltComposition = "Ceftriaxone 1000mg", category = "Injectable", mrp = 56.0, packaging = "1 Vial", isInjectable = true, isEssential = true),
    BrandMedicine(name = "Amicin 500mg Injection", brandName = "MACLEODS", saltComposition = "Amikacin 500mg/2ml", category = "Injectable", mrp = 68.0, packaging = "2ml Vial", isInjectable = true, isEssential = true)
  )

  private val glenmarkMedicines = listOf(
    BrandMedicine(name = "Ascoril-LS Syrup", brandName = "Glenmark", saltComposition = "Levosalbutamol + Ambroxol + Guaiphenesin", category = "Syrup", mrp = 120.0, packaging = "100ml Bottle"),
    BrandMedicine(name = "Candid-B Cream", brandName = "Glenmark", saltComposition = "Clotrimazole 1% + Beclomethasone 0.025%", category = "Ointment", mrp = 135.0, packaging = "20g Tube"),
    BrandMedicine(name = "Telma-H Tablet", brandName = "Glenmark", saltComposition = "Telmisartan 40mg + Hydrochlorothiazide 12.5mg", category = "Tablet", mrp = 195.0, packaging = "15 Tablets")
  )

  private val leefordMedicines = listOf(
    BrandMedicine(name = "Lee-P Tablet", brandName = "Leeford", saltComposition = "Aceclofenac 100mg + Paracetamol 325mg", category = "Tablet", mrp = 45.0, packaging = "10 Tablets"),
    BrandMedicine(name = "Leecob-OD Capsule", brandName = "Leeford", saltComposition = "Methylcobalamin 1500mcg + ALA", category = "Capsule", mrp = 120.0, packaging = "10 Capsules"),
    BrandMedicine(name = "Leepanto 40mg Injection", brandName = "Leeford", saltComposition = "Pantoprazole 40mg IV", category = "Injectable", mrp = 38.0, packaging = "1 Vial", isInjectable = true, isEssential = true)
  )

  private fun genericBrandFallback(brand: String): List<BrandMedicine> = listOf(
    BrandMedicine(name = "$brand Paracetamol 650mg", brandName = brand, saltComposition = "Paracetamol 650mg", category = "Tablet", mrp = 30.0, packaging = "10 Tablets", isEssential = true),
    BrandMedicine(name = "$brand Pantoprazole 40mg IV", brandName = brand, saltComposition = "Pantoprazole 40mg", category = "Injectable", mrp = 48.0, packaging = "1 Vial IV", isInjectable = true, isEssential = true),
    BrandMedicine(name = "$brand Ceftriaxone 1g Inj", brandName = brand, saltComposition = "Ceftriaxone 1000mg", category = "Injectable", mrp = 60.0, packaging = "1 Vial", isInjectable = true, isEssential = true),
    BrandMedicine(name = "$brand Amoxyclav 625", brandName = brand, saltComposition = "Amoxicillin 500mg + Clavulanic Acid 125mg", category = "Tablet", mrp = 180.0, packaging = "10 Tablets", isEssential = true),
    BrandMedicine(name = "$brand Cough Syrup", brandName = brand, saltComposition = "Dextromethorphan + Chlorpheniramine", category = "Syrup", mrp = 85.0, packaging = "100ml Bottle")
  )

  fun generateRichSubstitutesFallback(queryOrSalt: String): List<BrandMedicine> {
    return GeminiPharmacistService().generateRichSubstitutesFallback(queryOrSalt)
  }
}
