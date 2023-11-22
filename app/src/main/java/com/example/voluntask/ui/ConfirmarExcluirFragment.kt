package com.example.voluntask.ui

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.example.voluntask.R
import com.example.voluntask.databinding.FragmentConfirmarExcluirBinding
import com.example.voluntask.models.Evento
import com.example.voluntask.util.CustomToast
import com.example.voluntask.util.LoadingUI
import com.example.voluntask.util.Types
import com.example.voluntask.viewmodels.EventoViewModel

class ConfirmarExcluirFragment : Fragment() {

    private lateinit var binding: FragmentConfirmarExcluirBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentConfirmarExcluirBinding.inflate(inflater, container, false)
        val evento = arguments?.getParcelable<Evento>("evento")
        val viewModel = ViewModelProvider(this)[EventoViewModel::class.java]
        val customToast = CustomToast(binding.customToast, requireContext())
        var loadingUI: LoadingUI


        binding.btnDelete.setOnClickListener {
            loadingUI = LoadingUI(binding.btnDelete, binding.progressCircular, null)
            loadingUI.btnToLoading()
            viewModel.deleteEvento(evento!!.idEvento) {
                if (it) {
                    customToast.showCustomToast(
                        "Evento deletado com sucesso",
                        Types.SUCESS
                    )
                    val delayMillis = 2000L // 2 segundos

                    // Use um Handler para ocultar a Toast após o atraso
                    Handler(Looper.getMainLooper()).postDelayed({
                        Navigation.findNavController(binding.root)
                            .navigate(R.id.action_confirmarExcluirFragment_to_homeFragment)
                    }, delayMillis)
                } else {
                    // O registro não foi bem-sucedido
                    loadingUI.loadingToBtn()
                    customToast.showCustomToast("Erro ao deletar evento", Types.ERROR)
                }
            }
        }

        return binding.root
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ConfirmarExcluirFragment().apply {
                arguments = Bundle().apply {

                }
            }
    }
}