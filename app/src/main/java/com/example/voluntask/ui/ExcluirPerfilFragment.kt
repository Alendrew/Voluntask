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
import com.example.voluntask.databinding.FragmentExcluirPerfilBinding
import com.example.voluntask.models.Evento
import com.example.voluntask.models.Usuario
import com.example.voluntask.util.CustomToast
import com.example.voluntask.util.LoadingUI
import com.example.voluntask.util.Types
import com.example.voluntask.viewmodels.AuthViewModel
import com.example.voluntask.viewmodels.EventoViewModel

class ExcluirPerfilFragment : Fragment() {

    private lateinit var binding: FragmentExcluirPerfilBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentExcluirPerfilBinding.inflate(inflater, container, false)

        val usuario = arguments?.getParcelable<Usuario>("usuario")
        val viewModel = ViewModelProvider(this)[AuthViewModel::class.java]
        val customToast = CustomToast(binding.customToast, requireContext())
        var loadingUI: LoadingUI


        binding.btnDelete.setOnClickListener {
            loadingUI = LoadingUI(binding.btnDelete, binding.progressCircular, null)
            loadingUI.btnToLoading()
            if (usuario != null) {
                viewModel.deleteUser(usuario) {
                    if (it) {
                        customToast.showCustomToast(
                            "Usuário deletado com sucesso",
                            Types.SUCESS
                        )
                        val delayMillis = 2000L // 2 segundos

                        // Use um Handler para ocultar a Toast após o atraso
                        Handler(Looper.getMainLooper()).postDelayed({
                            Navigation.findNavController(binding.root)
                                .navigate(R.id.action_excluirPerfilFragment_to_loginFragment)
                        }, delayMillis)
                    } else {
                        // O registro não foi bem-sucedido
                        loadingUI.loadingToBtn()
                        customToast.showCustomToast("Erro ao deletar perfil", Types.ERROR)
                    }
                }
            }
        }
        return binding.root
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ExcluirPerfilFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}