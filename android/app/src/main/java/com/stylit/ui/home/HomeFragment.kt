package com.stylit.ui.home

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.internal.ViewUtils.dpToPx
import com.stylit.R
import com.stylit.databinding.FragmentBaseBinding
import com.stylit.databinding.FragmentHomeBinding
import okhttp3.*
import org.json.JSONObject
import java.io.IOException

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private lateinit var _bindingNavbar: FragmentBaseBinding
    private val bindingNavbar get() = _bindingNavbar!!

    private val binding get() = _binding!!
    private lateinit var sendButton: Button
    private lateinit var recyclerView: RecyclerView

    private lateinit var imageAdapter: ImageAdapter

    private lateinit var textView: TextView

    private lateinit var bottomNavigationView: BottomNavigationView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val view = binding.root
        sendButton = view.findViewById(R.id.sendButton)
        textView = view.findViewById(R.id.inputEditText)

        _bindingNavbar = FragmentBaseBinding.inflate(layoutInflater, container, false)
        bottomNavigationView = bindingNavbar.root.findViewById(R.id.bottom_nav)

        recyclerView = view.findViewById(R.id.messageRecyclerView)
        recyclerView.adapter = ImageAdapter(requireContext(), recyclerView).also { adapter ->
            imageAdapter = adapter
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val baseUrl = "http://192.168.0.177:8080/api/v1/images/"
        val generateUrl = baseUrl + "generate"
        val imageUrl = baseUrl //+ "2023-06-13_18-13-57_0.jpeg"

        sendButton.setOnClickListener {
            Toast.makeText(context, "You clicked me.", Toast.LENGTH_SHORT).show()
            Log.d("MyLogs", "button clicked")

            Log.d("MyLogs", textView.text.toString())
            if (textView.text.isEmpty()){
                Toast.makeText(context, "Insert some text to generate images", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val requestBody = JSONObject()
                .put("text", textView.text.toString())
                .put("num_images", 1)
                .toString()

            val request = Request.Builder()
                .url(generateUrl)
                .post(RequestBody.create(MediaType.parse("application/json"), requestBody))
                .build()

            val client = OkHttpClient()

            imageAdapter.addText(textView.text.toString())

            client.newCall(request).enqueue(object : Callback {
                override fun onResponse(call: Call, response: Response) {
                    if (response.isSuccessful) {
                        Log.d("MyLogs", "SUCCESS")

                        val responseBody = response.body()?.string()
                        Log.d("MyLogs", "Response Body: $responseBody")
                        val jsonObject = JSONObject(responseBody)
                        val generations = jsonObject.getJSONArray("generations")
                        if (generations.length() > 0) {
                            val filename = generations.getString(0)
                            val imageUrlWithFilename = imageUrl + filename
                            Log.d("MyLogs", "Image URL: $imageUrlWithFilename")

                            val imageRequest = Request.Builder()
                                .url(imageUrlWithFilename)
                                .get()
                                .build()

                            client.newCall(imageRequest).enqueue(object : Callback {
                                override fun onResponse(call: Call, response: Response) {
                                    if (response.isSuccessful) {
                                        Log.d("MyLogs", "Image Request SUCCESS")

                                        activity?.runOnUiThread {
                                            imageAdapter.addImage(imageUrlWithFilename)
                                        }
                                    } else {
                                        Log.d("MyLogs", "Image Request failed")
                                    }
                                }

                                override fun onFailure(call: Call, e: IOException) {
                                    Log.e("MyLogs", "Image Request failed: ${e.message}")
                                }
                            })
                        }
                    } else {
                        Log.d("MyLogs", "Generate Request failed")
                    }
                }

                override fun onFailure(call: Call, e: IOException) {
                    Log.e("MyLogs", "Generate Request failed: ${e.message}")
                }
            })
        }
    }

    private fun dpToPx(context: Context, dp: Int): Int {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp.toFloat(), context.resources.displayMetrics).toInt()
    }

    private fun extractFilename(url: String): String {
        val slashIndex = url.lastIndexOf('/')
        if (slashIndex != -1 && slashIndex < url.length - 1) {
            return url.substring(slashIndex + 1)
        }
        return ""
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
