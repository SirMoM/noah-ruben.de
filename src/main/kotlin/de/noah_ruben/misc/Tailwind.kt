package de.noah_ruben.misc

fun borderGray(scale: String) = "border-black-$scale"

@OptIn(ExperimentalStdlibApi::class)
fun String.colorFromString(): String = this.hashCode().toHexString(format = HexFormat.UpperCase).removeRange(0, 2)

@OptIn(ExperimentalStdlibApi::class)
fun String.invertedFromString(): String {
    return this.hexToUInt().xor(16777215u).toHexString(format = HexFormat.UpperCase).removeRange(0, 2) // 0xFFFFFF as UInt
}
