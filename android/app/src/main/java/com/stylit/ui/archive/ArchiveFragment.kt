import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.GridView
import com.stylit.R
import com.stylit.databinding.FragmentArchiveBinding
import com.stylit.ui.archive.ArchiveAdapter
import com.stylit.ui.archive.FullScreenActivity

class ArchiveFragment : Fragment() {

    private var _binding: FragmentArchiveBinding? = null
    private val binding get() = _binding!!
    private lateinit var gridView: GridView
    private lateinit var imageUris: List<Uri>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentArchiveBinding.inflate(inflater, container, false)

        val savedImagesDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
        imageUris = savedImagesDirectory.listFiles()?.map { file -> Uri.fromFile(file) } ?: emptyList()

        val view = binding.root
        gridView = view.findViewById(R.id.gridView)
        gridView.adapter = ArchiveAdapter(requireContext(), imageUris.map { uri -> uri.toString() })

        gridView.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
            val intent = Intent(requireContext(), FullScreenActivity::class.java)
            intent.putExtra("id", position)
            startActivity(intent)
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        /*
        binding.buttonFirst.setOnClickListener {
            findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
        }
         */
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
