package com.example.voluntask.ui

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.voluntask.R
import com.example.voluntask.databinding.FragmentHome2Binding
import com.example.voluntask.databinding.FragmentInscricaoBinding
import com.example.voluntask.models.Evento
import com.example.voluntask.models.enums.Categorias
import com.example.voluntask.models.enums.Status
import com.example.voluntask.models.enums.TipoConta
import com.example.voluntask.viewmodels.SharedViewModel
import java.time.LocalDate
import java.util.Calendar


class InscricaoFragment : Fragment() {

    private lateinit var binding: FragmentInscricaoBinding
    private lateinit var sharedViewModel: SharedViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val evento = arguments?.getParcelable<Evento>("evento")
        binding = FragmentInscricaoBinding.inflate(inflater, container, false)
        sharedViewModel = ViewModelProvider(requireActivity())[SharedViewModel::class.java]

        if (evento!!.status == Status.ENCERRADO) {
            binding.btnRegister.isEnabled = false
            binding.btnRegister.setBackgroundColor(Color.parseColor("#737373"))
        }

        sharedViewModel.usuario.observe(viewLifecycleOwner) { usuario ->
            binding.nomeInst.setText(usuario.nome)
        }

        binding.dataS.setText(
            "${"%02d".format(LocalDate.parse(evento.dataInicio!!).dayOfMonth)}/${
                "%02d".format(LocalDate.parse(evento.dataInicio!!).month + 1)
            }/${LocalDate.parse(evento.dataInicio!!).year}"
        )


        binding.dataE.setText(
            "${"%02d".format(LocalDate.parse(evento.dataFim!!).dayOfMonth)}/${
                "%02d".format(LocalDate.parse(evento.dataFim!!).month + 1)
            }/${LocalDate.parse(evento.dataFim!!).year}"
        )


        binding.categoria.setText(
            when (evento.categoria) {
                Categorias.DOACAO -> "Doação"
                Categorias.LIMPEZA -> "Limpeza"
                Categorias.CARIDADE -> "Caridade"
            }
        )

        binding.status.setText(
            when (evento.status) {
                Status.ATIVO -> "Ativo"
                Status.ENCERRADO -> "Encerrado"
            }
        )



        binding.name.setText(evento.nome)
        binding.local.setText(evento.localizacao)
        binding.desc.setText(evento.descricao)

        binding.btnRegister.setOnClickListener {

        }

        return binding.root
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            InscricaoFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}