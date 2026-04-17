package com.ercoding.foodify.data.remote


fun buildNutritionQuery(query: String): String {
    return "gib mir die folgende werte von $query als json. kein text davor oder danach. wenn die menge unklar ist, schäte eine typische portion: calories\n" +
            "protein\n" +
            "fat\n" +
            "saturatedFat\n" +
            "unsaturatedFat\n" +
            "carbohydrates\n" +
            "sugar\n" +
            "fiber\n" +
            "salt\n" +
            "cholesterol\n" +
            "sodium\n" +
            "potassium\n" +
            "vitaminA\n" +
            "vitaminB6\n" +
            "vitaminB12\n" +
            "vitaminC\n" +
            "vitaminD\n" +
            "vitaminE\n" +
            "vitaminK\n" +
            "folicAcid\n" +
            "calcium\n" +
            "iron\n" +
            "magnesium\n" +
            "zinc\n" +
            "phosphorus\n" +
            "selenium\n" +
            "copper\n" +
            "manganese\n" +
            "omega3\n" +
            "omega6"
}
