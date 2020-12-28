package live.tek.form_data



data class Result(
    val message:String,
    val data:List<DataItem>
)


data class DataItem(val url:String, val id:String)