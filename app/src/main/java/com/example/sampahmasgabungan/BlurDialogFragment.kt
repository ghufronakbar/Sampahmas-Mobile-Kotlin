import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import androidx.fragment.app.DialogFragment
import com.example.sampahmasgabungan.R
import jp.wasabeef.blurry.Blurry

class BlurDialogFragment : DialogFragment() {

    private lateinit var qrCodeBitmap: Bitmap

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_blur_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Menangani efek blur
        Blurry.with(requireContext())
            .radius(10)
            .sampling(8)
            .async()
            .capture(view.findViewById(R.id.blurView))
            .into(view.findViewById(R.id.blurView))

        // Mengatur efek gelap
        view.findViewById<View>(R.id.backgroundDim).alpha = 0.5f

        // Menampilkan QR code
        view.findViewById<ImageView>(R.id.qrCodeImageView).setImageBitmap(qrCodeBitmap)

        // Menangani tombol close
        view.findViewById<ImageButton>(R.id.btnClose).setOnClickListener {
            dismiss()
        }
    }

    fun setQrCodeBitmap(bitmap: Bitmap) {
        qrCodeBitmap = bitmap
    }

    companion object {
        fun newInstance(): BlurDialogFragment {
            return BlurDialogFragment()
        }
    }
}