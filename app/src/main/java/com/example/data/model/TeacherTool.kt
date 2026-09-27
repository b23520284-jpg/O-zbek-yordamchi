package com.example.data.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Translate
import androidx.compose.ui.graphics.vector.ImageVector

enum class TeacherToolType(
    val numericId: Int,
    val id: String,
    val buttonNumber: String,
    val title: String,
    val shortDesc: String,
    val defaultMaxTokens: Int,
    val icon: ImageVector,
    val defaultPromptTemplate: String
) {
    KONSPEKT(
        numericId = 2,
        id = "konspekt",
        buttonNumber = "ID: 2",
        title = "Dars ishlanmasi va konspekt",
        shortDesc = "45 daqiqalik konspekt (5 bosqichli)",
        defaultMaxTokens = 600,
        icon = Icons.AutoMirrored.Filled.Assignment,
        defaultPromptTemplate = """Siz Oʻzbek tili metodistisiz. Quyidagi mavzu boʻyicha professional, 45 daqiqalik dars konspektini tuzing.
Mavzu: [Mavzu_Nomi] | Sinf: [Sinf_Raqami]

Javobni faqat ushbu shablonda chiqaring:
1. Dars maqsadi (Ta'limiy, tarbiyaviy, rivojlantiruvchi - jami 3 ta qisqa jumla).
2. Oʻtilgan darsni takrorlash (3 ta qisqa va lo'nda savol).
3. Yangi mavzu bayoni (Eng asosiy qoidalar tezis ko'rinishida).
4. Mustahkamlash (1 ta amaliy mashq va 1 ta qisqa interaktiv o'yin).
5. Uyga vazifa.
Hech qanday ortiqcha bezak va chiroyli gaplar ishlatilmasin."""
    ),
    TAHLIL(
        numericId = 3,
        id = "tahlil",
        buttonNumber = "ID: 3",
        title = "Matn va she'rlar lingvistik tahlili",
        shortDesc = "Mazmun, lug'at ishi va til xususiyatlari",
        defaultMaxTokens = 400,
        icon = Icons.AutoMirrored.Filled.MenuBook,
        defaultPromptTemplate = """Siz filologiya fanlari mutaxassisiz. Berilgan matn/g'azalni tahlil qilib, faqat ushbu shablonda chiqaring:
Matn/G'azal: "[Oʻqituvchi_Matni]"

- Mazmuni: (Maksimal 2 ta gapda matn yoki she'r mohiyati).
- Lug'at ishi: (Matndagi tushunilishi qiyin bo'lgan ko'pi bilan 3 ta arxaik/forsiy so'z va ularning qisqa izohi).
- Til xususiyatlari: (Matndagi eng asosiy gap bo'laklari, so'z turkumlari yoki badiiy san'atlar (tashbeh, istiora) ro'yxati).
Boshqa hech qanday qo'shimcha matn yozmang."""
    ),
    TEST(
        numericId = 4,
        id = "test",
        buttonNumber = "ID: 4",
        title = "Avtomatik test generatori",
        shortDesc = "5 ta 4 variantli test va kalit",
        defaultMaxTokens = 350,
        icon = Icons.Default.Quiz,
        defaultPromptTemplate = """Siz testologsiz. Berilgan mavzudan kelib chiqib faqat 5 ta 4 variantli test chiqaring.
Mavzu: [Mavzu_Nomi]

Shablon qat'iy ravishda quyidagicha bo'lsin:
[Savol raqami]. [Savol matni]?
A) Variant 1
B) Variant 2
C) Variant 3
D) Variant 4

Eng oxirgi qatorda "To'g'ri javoblar kaliti:" deb faqat raqam va harfni yozing (Masalan: 1-A, 2-C, 3-B...). Boshqa matn qo'shmang."""
    ),
    LUGAT(
        numericId = 5,
        id = "lugat",
        buttonNumber = "ID: 5",
        title = "Izohli lug'at va tarjimon",
        shortDesc = "Lug'aviy ma'no, adabiy misol va tarjima",
        defaultMaxTokens = 200,
        icon = Icons.Default.Translate,
        defaultPromptTemplate = """Siz akademik lug'at tizimisiz. Berilgan soʻzning izohini faqat ushbu shablonda chiqaring:
Soʻz/Ibora: [Soʻz_Nomi]

- Lug'aviy ma'nosi: (Maksimal 2 ta jumla).
- Adabiy misol: (Shu so'z qatnashgan bitta adabiy gap yoki parcha).
- Tarjimasi: (Ruscha: [tarjima] | Inglizcha: [tarjima]).
Boshqa hech qanday so'z qo'shmang."""
    ),
    DIKTANT(
        numericId = 7,
        id = "diktant",
        buttonNumber = "ID: 7",
        title = "Ovozli diktant tahriri",
        shortDesc = "Ovozli/xom matn imlo va tinish belgilari",
        defaultMaxTokens = 1000,
        icon = Icons.Default.Mic,
        defaultPromptTemplate = """Siz mukammal oʻzbek imlo va punktuatsiya redaktorisiz. Quyidagi matn mikrofondan ovoz orqali yozib olingan. Matnning mazmunini mutloq o'zgartirmasdan, uni faqat o'zbek imlo va punktuatsiya (tinish belgilari) qoidalariga solib, tayyor to'g'rilangan matnning o'zini qaytaring. Izoh, tushuntirish yoki "mana matn" degan gaplar mutloq yozilmasin.
Xom matn: "[Ovozdan_Kelgan_Matn]""""
    ),
    MENTOR(
        numericId = 10,
        id = "mentor",
        buttonNumber = "ID: 10",
        title = "Shaxsiy metodik yordamchi (Mentor)",
        shortDesc = "3 ta eng muhim amaliy maslahat",
        defaultMaxTokens = 400,
        icon = Icons.Default.School,
        defaultPromptTemplate = """Siz pedagogika va psixologiya professorisiz. Oʻqituvchi aytgan pedagogik muammoga amaliy yechim bering. Nazariyani tashlab, faqat 3 ta eng muhim va darsda qo'llash mumkin bo'lgan amaliy maslahatni (punktlar bilan) qisqa qilib yozing.
Muammo: [Pedagogik_Muammo]"""
    );

    fun resolveMaxTokens(inputText: String = ""): Int {
        return when (this) {
            DIKTANT -> {
                // [Maksimal limit: Kiruvchi matn hajmida]
                val estimatedTokens = (inputText.length / 3).coerceIn(150, 1500)
                estimatedTokens
            }
            else -> defaultMaxTokens
        }
    }

    companion object {
        fun fromId(id: String): TeacherToolType {
            return entries.firstOrNull { it.id == id } ?: KONSPEKT
        }

        fun fromNumericId(numericId: Int): TeacherToolType {
            return entries.firstOrNull { it.numericId == numericId } ?: KONSPEKT
        }
    }
}
