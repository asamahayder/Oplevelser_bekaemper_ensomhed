package group24.oplevelserbekaemperensomhed.logic.firebase

interface MyUploadPicturesListener {
    fun onSuccess(`object`: Any)
    fun onProgress(`object`: Any)
    fun onFailure(`object`: Any)
}