package com.kire.audio.presentation.util.search

/**
 * Вычисляет степень схожести 2-х символьных последовательностей.
 * Чем ближе число к единице, тем сильнее совпадают символьные последовательности.
 *
 * @param lhs Первая символьная последовательность
 * @param rhs Вторая символьная последовательность
 *
 * @return Отношение Левенштейна: 1.0 - расстояние Левенштейна / максимум из длин 2-х последовательностей
 *
 * @author Михаил Гонтарев (KiREHwYE)
 */
fun levenshteinRatio(lhs: CharSequence, rhs: CharSequence): Double {

    /** Находим максимум из длин 2-х последовательностей */
    val maxLen = maxOf(lhs.length, rhs.length)

    /** Если длина обоих 0, то обе последовательности - пустые строки, то есть совпадают */
    if (maxLen == 0)
        return 1.0

    /** Расстояние Левенштейна */
    val distance = levenshteinDistance(lhs, rhs)

    /** Находим степень схожести 2-х строк.
     * Чем ближе число к единице, тем сильнее совпадают символьные последовательности
     * */
    return 1.0 - distance.toDouble() / maxLen
}