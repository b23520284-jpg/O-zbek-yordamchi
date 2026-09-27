package com.example.data.local

object PreloadedData {
    val initialCaches = listOf(
        TeacherCacheEntity(
            cacheKey = "KONSPEKT::Ot soʻz turkumi::7-sinf",
            toolType = "KONSPEKT",
            queryTitle = "Ot soʻz turkumi (7-sinf)",
            promptSent = "Mavzu: Ot soʻz turkumi | Sinf: 7-sinf",
            responseContent = """1. Dars maqsadi:
- Ta'limiy: O'quvchilarga ot so'z turkumining leksik-semantik belgilari va gapdagi sintaktik vazifalarini to'laqonli o'rgatish.
- Tarbiyaviy: Ona tilimizning boy lug'at tarkibiga ehtirom va milliy muloqot odobini tarbiyalash.
- Rivojlantiruvchi: Matndan otlarni tezkor aniqlash, so'roq berish va nutqda to'g'ri qo'llash ko'nikmasini rivojlantirish.

2. Oʻtilgan darsni takrorlash:
- Mustaqil so'z turkumlari qaysi jihatlariga ko'ra yordamchi so'zlardan ajralib turadi?
- Ot boshqa turkumlardan qanday so'roqlari orqali farqlanadi?
- Atoqli va turdosh otlarning imlosidagi asosiy qoidalar nimalardan iborat?

3. Yangi mavzu bayoni:
- Ot – shaxs, narsa-buyum, o'rin-joy va mavhum tushunchalar nomini bildiruvchi mustaqil so'z turkumi.
- Kim? nima? qayer? so'roqlariga javob bo'ladi.
- Otlar yakka va to'da otlarga, aniq va mavhum otlarga ajraladi.
- Gap ichida asosan ega, to'ldiruvchi, aniqlovchi va ot-kesim vazifasida keladi.

4. Mustahkamlash:
- Amaliy mashq: Darslikdagi matndan 6 ta otni ajratib, ularning atoqli/turdosh hamda shaxs/narsa belgilarini jadvalga joylashtiring.
- Interaktiv o'yin: "Kim ko'p topadi?" – 2 daqiqa ichida har bir guruh berilgan harf bilan boshlanadigan atoqli va turdosh otlar zanjirini tuzadi.

5. Uyga vazifa:
- 142-mashqni bajarish hamda o'qilayotgan badiiy asardan 5 ta ot qatnashgan gapni ko'chirib, sintaktik tahlil qilish.""",
            timestamp = System.currentTimeMillis() - 86400000,
            tokensSaved = 380,
            isPreloaded = true
        ),
        TeacherCacheEntity(
            cacheKey = "TAHLIL::Gʻurbatda gʻarib shodmon boʻlmas emish",
            toolType = "TAHLIL",
            queryTitle = "Alisher Navoiy ruboiysi tahlili",
            promptSent = "Matn/G'azal: Gʻurbatda gʻarib shodmon boʻlmas emish...",
            responseContent = """- Mazmuni: Inson o'z ona yurtidan yiroqda, g'urbatda qanchalik farovon yashamasin, hech qachon chin dildan baxtiyor bo'la olmaydi; haqiqiy baxt va xotirjamlik faqat Vatanda bo'ladi.
- Lug'at ishi:
1. G'arib – musofir, o'z yurtidan olisda yolg'iz qolgan kimsa.
2. Shodmon – xursand, shod, quvnoq, baxtiyor.
3. Oshyon – in, uya, boshpana, makon.
- Til xususiyatlari:
1. Badiiy san'at: Tazod (shodmon va g'arib holat ziddiyati), tamsil (bulbul va tikan timsoli orqali inson va g'urbat holati).
2. Qofiya tuzilishi: a-a-b-a ruboiy qolipi, "...bo'lmas emish" radif.
3. So'z turkumlari: "G'urbatda" – o'rin holi (ot), "shodmon" – sifat/holat, "oltun" – sifatlovchi aniqlovchi.""",
            timestamp = System.currentTimeMillis() - 43200000,
            tokensSaved = 290,
            isPreloaded = true
        ),
        TeacherCacheEntity(
            cacheKey = "TEST::Fe'l zamonlari (7-sinf)",
            toolType = "TEST",
            queryTitle = "Fe'l zamonlari (7-sinf)",
            promptSent = "Mavzu: Fe'l zamonlari",
            responseContent = """1. O'tgan zamon fe'lini yasovchi asosiy qo'shimchalarni aniqlang?
A) -di, -gan, -gan edi
B) -yapti, -moqda, -yotir
C) -ajak, -moqchi, -ar
D) -sa, -gach, -guncha

2. "U kecha kitobni diqqat bilan o'qib chiqdi" gapidagi fe'l qaysi zamonda?
A) Hozirgi zamon
B) Yaqin o'tgan zamon
C) Kelasi zamon
D) Maqsad kelasi zamon

3. Hozirgi zamon davom fe'lining yasovchisi to'g'ri ko'rsatilgan qatorni toping?
A) -di
B) -yapti, -moqda
C) -ajak
D) -gach

4. "Ertaga barcha o'quvchilar kutubxonaga borishadi" gapidagi fe'l qaysi zamonda?
A) O'tgan zamon
B) Hozirgi zamon
C) Kelasi zamon
D) Shart mayli

5. Qaysi qatordagi fe'l uzoq o'tgan zamon shaklida berilgan?
A) Borayotir
B) Yozgan edi
C) Keladi
D) Ko'rmoqda

To'g'ri javoblar kaliti: 1-A, 2-B, 3-B, 4-C, 5-B""",
            timestamp = System.currentTimeMillis() - 7200000,
            tokensSaved = 310,
            isPreloaded = true
        ),
        TeacherCacheEntity(
            cacheKey = "LUGAT::Ma'rifat",
            toolType = "LUGAT",
            queryTitle = "Ma'rifat",
            promptSent = "Soʻz/Ibora: Ma'rifat",
            responseContent = """- Lug'aviy ma'nosi: Ilm-fan, ta'lim-tarbiya va madaniyat orqali aql va qalbning yorishishi, inson kamolotini belgilovchi yuksak ma'naviy tushuncha.
- Adabiy misol: "Jaholatga qarshi faqat ma'rifat bilan kurashmoq lozimdir."
- Tarjimasi: (Ruscha: просвещение | Inglizcha: enlightenment).""",
            timestamp = System.currentTimeMillis() - 3600000,
            tokensSaved = 140,
            isPreloaded = true
        ),
        TeacherCacheEntity(
            cacheKey = "MENTOR::O'quvchilar uyga vazifa bajarishga qiziqmayapti",
            toolType = "MENTOR",
            queryTitle = "Uyga vazifa bajarmaslik muammosi",
            promptSent = "Muammo: O'quvchilar uyga vazifa bajarishga qiziqmayapti",
            responseContent = """1. Vazifani shablon mashqlardan ijodiy loyihalarga aylantiring: qoidani yodlash o'rniga, oila a'zolaridan mavzuga oid 3 daqiqali audio-intervyu olish yoki qisqa matn tuzishni topshiring.
2. Uch bosqichli tanlov (differensial) tizimini bering: oddiy, qiziqarli yoki murakkab-ijodiy topshiriqdan o'quvchi o'z ixtiyori bilan birini tanlasin.
3. Darsning ilk 3 daqiqasida vazifadagi eng yaxshi topilmalarni 'Kun iqtibosi' sifatida dars taxtasiga yozib, o'quvchini rag'batlantiring.""",
            timestamp = System.currentTimeMillis() - 1800000,
            tokensSaved = 210,
            isPreloaded = true
        )
    )
}
