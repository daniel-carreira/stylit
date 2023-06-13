package com.stylit.ui.home

import com.stylit.RequestHandler
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import com.stylit.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private val requestHandler = RequestHandler()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.sendButton.setOnClickListener {
            val url = "https://www.google.com/"
            val requestData = "{ \"key\": \"value\" }"

            requestHandler.getRequest(url) { response ->
                if (response.isSuccessful) {
                    val responseData = response.body()?.string()
                    println(responseData)
                } else {
                    println(response)
                }
            }
            /*
            requestHandler.postRequest(url, requestData) { response ->
                // Process the response as needed
                if (response.isSuccessful) {
                    val responseData = response.body()?.string()
                    println(responseData)
                } else {
                    println(response)
                }
            }
            */
        }
    }
}
