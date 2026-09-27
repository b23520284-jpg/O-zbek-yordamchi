package com.example.data.model

data class HistoricalEra(
    val id: String,
    val period: String,
    val title: String,
    val subtitle: String,
    val description: String,
    val keyFigures: String,
    val iconicQuote: String,
    val quoteAuthor: String,
    val badge: String
)

object HistoricalData {
    val eras = listOf(
        HistoricalEra(
            id = "navoiy",
            period = "XV asr (Temuriylar Renessansi)",
            title = "Alisher Navoiy va Turkiy Til Saltanati",
            subtitle = "Turkiy til gavhari va 'Muhokamat ul-lug'atayn'",
            description = "Alisher Navoiy turkiy tilning cheksiz boyligini butun olamga isbotladi. 'Xamsa' yaratib, turkiy tilni jahon adabiyotining eng yuksak cho'qqisiga olib chiqdi. Har bir ona tili darsi Navoiy merosidan kuch oladi.",
            keyFigures = "Hazrat Alisher Navoiy, Abdurahmon Jomiy, Husayn Boyqaro",
            iconicQuote = "Tilga ixtiyorsiz – elga e'tiborsiz.",
            quoteAuthor = "Alisher Navoiy",
            badge = "Boshlang'ich Nuqta"
        ),
        HistoricalEra(
            id = "mumtoz",
            period = "XVI – XIX asrlar",
            title = "Bobur Mirzo va Mumtoz Adabiy Maktab",
            subtitle = "Lutf, go'zallik va nazmiy kamolot",
            description = "Zahiriddin Muhammad Boburning 'Boburnoma' asari o'zbek nasrining bebaho namunasi bo'ldi. Mashrab, Nodirabegim, Uvaysiy, Ogahiy, Muqimiy va Furqatlar ona tilimizning musiqiyligi va nozik ifoda qudratini yanada boyitdi.",
            keyFigures = "Bobur Mirzo, Mashrab, Nodirabegim, Ogahiy, Furqat",
            iconicQuote = "Tole' yoʻqi jonimga baloligʻ boʻldi, Har ishniki ayladim – xatoligʻ boʻldi...",
            quoteAuthor = "Zahiriddin Muhammad Bobur",
            badge = "Mumtoz Meros"
        ),
        HistoricalEra(
            id = "jadid",
            period = "XX asr boshlari",
            title = "Jadid Ma'rifatparvarligi va Yangi Adabiyot",
            subtitle = "'Til yashasa – millat yashar!'",
            description = "Mahmudxo'ja Behbudiy, Abdulla Qodiriy, Abdulhamid Cho'lpon va Abdurauf Fitrat ona tilini millatning ruhi va taraqqiyot asosi deb bildi. O'zbek romanining asoschisi Qodiriy 'O'tkan kunlar' bilan tilimizning yangi nasriy ufqlarini ochdi.",
            keyFigures = "Abdulla Qodiriy, Cho'lpon, Fitrat, Behbudiy, Munavvarqori",
            iconicQuote = "Ona tilini yo'qotgan millat o'zligini yo'qotadi.",
            quoteAuthor = "Abdulhamid Cho'lpon",
            badge = "Ma'rifat Uyg'onishi"
        ),
        HistoricalEra(
            id = "davlat_tili",
            period = "1989-yil 21-oktabr",
            title = "Davlat Tili Maqomi va Milliy G'urur",
            subtitle = "Ona tilimizning qonuniy qayta tug'ilishi",
            description = "1989-yil 21-oktabrda 'Davlat tili haqida'gi qonun qabul qilindi. Erkin Vohidov, Abdulla Oripov, Halima Xudoyberdiyeva va boshqa adiblar ona tilimizning huquqiy va ma'naviy mustaqilligi uchun qalam tebratdilar.",
            keyFigures = "Erkin Vohidov, Abdulla Oripov, Muhammad Yusuf",
            iconicQuote = "Ona tilim, kechir meni, ona tilim...",
            quoteAuthor = "Abdulla Oripov",
            badge = "Huquqiy Maqom"
        ),
        HistoricalEra(
            id = "zamonaviy_ai",
            period = "XXI asr (Bugungi Yangi O'zbekiston)",
            title = "Raqamli Maktab va AI Texnologiyalari",
            subtitle = "O'qituvchi mehnati va Sun'iy Intellekt uyg'unligi",
            description = "Bugun o'zbek tili raqamli axborot makoniga dadil kirib bormoqda. O'qituvchining har bir darsi, konspekti, testi va tahlili sun'iy intellekt orqali sekundlarda yaratiladi, tokenlar 0-kesh bilan tejaladi.",
            keyFigures = "Zamondosh O'zbek Tili O'qituvchilari & Metodistlar",
            iconicQuote = "Milliy tilimiz – bizning xalqaro minbarimiz va raqamli kelajagimiz poydevoridir.",
            quoteAuthor = "Yangi O'zbekiston Ta'lim Shiori",
            badge = "Bugun & Kelajak"
        )
    )

    val inspirationalQuotes = listOf(
        "G'aroyibot olamiga yo'l – ona tilidan boshlanadi. (Hazrat Navoiy)",
        "Ona tilimiz – ming yillik hikmatlar bulog'i va kelajak yo'lchirog'idir.",
        "Har bir dars – millat tafakkuriga qo'yilgan oltin poydevordir.",
        "So'z durlarini terish – o'qituvchining eng sharafli san'atidir."
    )
}
