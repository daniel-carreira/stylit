import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Button
import android.widget.GridView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.fragment.app.Fragment
import com.stylit.R
import com.stylit.databinding.FragmentArchiveBinding
import com.stylit.adapter.ArchiveAdapter
import com.stylit.ui.archive.FullScreenActivity
import com.stylit.ui.archive.TakePhoto

class ArchiveFragment : Fragment() {

    private var _binding: FragmentArchiveBinding? = null
    private val binding get() = _binding!!
    private lateinit var gridView: GridView
    private lateinit var imageUris: List<Uri>
    private lateinit var buttonPhoto: Button

    private val takephotoFragment = TakePhoto()
    private lateinit var mPermissionResultLauncher: ActivityResultLauncher<Array<String>>


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

        buttonPhoto = view.findViewById(R.id.addPhotoButton)

        return view
    }

    fun onImageClick(view: View) {
        // Handle the click event here
        Log.d("MyLogs", "Clickeeeeddd")

    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        gridView.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
            val intent = Intent(requireContext(), FullScreenActivity::class.java)
            intent.putExtra("uri", imageUris[position].path)
            startActivity(intent)
        }

        buttonPhoto.setOnClickListener {
            Toast.makeText(requireContext(), "You clicked me.", Toast.LENGTH_SHORT).show()
            val fragmentManager = requireActivity().supportFragmentManager
            fragmentManager.beginTransaction()
                .replace(R.id.container, takephotoFragment)
                .commit()
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
