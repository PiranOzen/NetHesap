package com.example.nethesap.data.remote.dto

import org.simpleframework.xml.Attribute
import org.simpleframework.xml.Element
import org.simpleframework.xml.ElementList
import org.simpleframework.xml.Root

@Root(name = "Tarih_Date", strict = false)
data class CurrencyResponse @JvmOverloads constructor(
    @field:ElementList(inline = true, entry = "Currency", required = false)
    var currencyList: List<CurrencyDto>? = null
)

@Root(name = "Currency", strict = false)
data class CurrencyDto @JvmOverloads constructor(
    @field:Attribute(name = "CurrencyCode", required = false)
    var code: String? = null,

    @field:Element(name = "Isim", required = false)
    var name: String? = null,

    @field:Element(name = "ForexBuying", required = false)
    var buying: String? = null,

    @field:Element(name = "ForexSelling", required = false)
    var selling: String? = null
)
