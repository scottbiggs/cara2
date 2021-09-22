package com.sleepfuriously.cara2

import android.content.Context
import com.android.volley.*
import com.android.volley.toolbox.HttpHeaderParser
import com.android.volley.toolbox.Volley
import java.io.ByteArrayOutputStream
import java.io.DataOutputStream
import java.io.IOException

/**
 * Comes from here:
 *      https://stackoverflow.com/a/58012895/624814
 *
 *  Doesn't seem to work, sigh.
 */
class MultipartWebservice(context: Context) {

    private var queue: RequestQueue? = null

    private val boundary = "apiclient-" + System.currentTimeMillis()
    private val mimeType = "multipart/form-data;boundary=$boundary"

    init {
        queue = Volley.newRequestQueue(context)
    }


    fun sendMultipartRequest(
        method: Int,
        url: String,
        fileData: ByteArray,
        fileName: String,
        listener: Response.Listener<NetworkResponse>,
        errorListener: Response.ErrorListener
    ) {

        // Create multi part byte array
        val bos = ByteArrayOutputStream()
        val dos = DataOutputStream(bos)
        buildMultipartContent(dos, fileData, fileName)
        val multipartBody = bos.toByteArray()

        // Request header, if needed
        val headers = HashMap<String, String>()
        headers["API-TOKEN"] = "458e126682d577c97d225bbd73a75b5989f65e977b6d8d4b2267537019ad9d20"

        val request = MultipartRequest(
            method,
            url,
            errorListener,
            listener,
            headers,
            mimeType,
            multipartBody
        )

        queue?.add(request)

    }

    @Throws(IOException::class)
    private fun buildMultipartContent(dos: DataOutputStream, fileData: ByteArray, fileName: String) {

        val twoHyphens = "--"
        val lineEnd = "\r\n"

        dos.writeBytes(twoHyphens + boundary + lineEnd)
        dos.writeBytes("Content-Disposition: form-data; name=\"file\"; filename=\"$fileName\"$lineEnd")
        dos.writeBytes(lineEnd)
        dos.write(fileData)
        dos.writeBytes(lineEnd)
        dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd)
    }

    class MultipartRequest(
        method: Int,
        url: String,
        errorListener: Response.ErrorListener?,
        private var listener: Response.Listener<NetworkResponse>,
        private var headers: MutableMap<String, String>,
        private var mimeType: String,
        private var multipartBody: ByteArray
    ) : Request<NetworkResponse>(method, url, errorListener) {

        override fun getHeaders(): MutableMap<String, String> {
            return if (headers.isEmpty()) super.getHeaders() else headers
        }

        override fun getBodyContentType(): String {
            return mimeType
        }

        override fun getBody(): ByteArray {
            return multipartBody
        }

        override fun parseNetworkResponse(response: NetworkResponse?): Response<NetworkResponse> {
            return try {
                Response.success(response, HttpHeaderParser.parseCacheHeaders(response))
            } catch (e: Exception) {
                Response.error(ParseError(e))
            }
        }

        override fun deliverResponse(response: NetworkResponse?) {
            listener.onResponse(response)
        }
    }
}