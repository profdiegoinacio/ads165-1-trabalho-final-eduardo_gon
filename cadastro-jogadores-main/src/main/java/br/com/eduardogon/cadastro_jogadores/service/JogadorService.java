package br.com.eduardogon.cadastro_jogadores.service;

import java.util.List;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.fasterxml.jackson.core.JsonProcessingException;

import br.com.eduardogon.cadastro_jogadores.model.GrupoCodinome;
import br.com.eduardogon.cadastro_jogadores.model.Jogador;
import br.com.eduardogon.cadastro_jogadores.repository.JogadorRepository;

@Service
public class JogadorService {
  private final JogadorRepository jogadorRepository;
  private final CodinomeService codinomeService;

  public JogadorService(JogadorRepository jogadorRepository, CodinomeService codinomeService) {
    this.jogadorRepository = jogadorRepository;
    this.codinomeService = codinomeService;
  }

  @Transactional
  public Jogador registrarJogador(Jogador jogador) throws JsonProcessingException {
    var codinomesEmUso = listarCodinomesEmUso(jogador.grupoCodinome());
    var novoCodinome = codinomeService.gerarCodinome(jogador.grupoCodinome(), codinomesEmUso);
    var novoJogador = jogador.withCodinome(novoCodinome);
    return jogadorRepository.salvar(novoJogador);
  }

  public List<Jogador> listarJogadores() {
    return jogadorRepository.listarJogadores();
  }

  private List<String> listarCodinomesEmUso(GrupoCodinome grupoCodinome) {
    return jogadorRepository.listarCodinomesEmUso(grupoCodinome);
  }

  @Transactional
  public Jogador atualizarJogador(Jogador jogadorAtualizado) {
    var jogadores = listarJogadores();

    // Garante que não existe outro jogador com esse codinome e grupo
    boolean codinomeEmUsoPorOutro = jogadores.stream()
        .anyMatch(j -> !j.email().equalsIgnoreCase(jogadorAtualizado.email()) &&
            j.codinome().equalsIgnoreCase(jogadorAtualizado.codinome()) &&
            j.grupoCodinome().equals(jogadorAtualizado.grupoCodinome()));

    if (codinomeEmUsoPorOutro) {
      throw new ResponseStatusException(HttpStatus.CONFLICT,
          "Já existe outro jogador com esse codinome no mesmo grupo.");
    }

    try {
      jogadorRepository.atualizar(jogadorAtualizado);
      return jogadorAtualizado;
    } catch (DuplicateKeyException e) {
      throw new ResponseStatusException(HttpStatus.CONFLICT,
          "Violação de chave única ao atualizar jogador", e);
    }
  }
  @Transactional
  public void excluirJogador(String email) {
      jogadorRepository.excluirPorEmail(email);
  }
  

}
