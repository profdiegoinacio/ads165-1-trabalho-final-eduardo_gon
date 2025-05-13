package br.com.eduardogon.cadastro_jogadores.web;

import br.com.eduardogon.cadastro_jogadores.model.Jogador;
import br.com.eduardogon.cadastro_jogadores.service.JogadorService;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/jogadores")
public class JogadorRestController {

    private final JogadorService jogadorService;

    public JogadorRestController(JogadorService jogadorService) {
        this.jogadorService = jogadorService;
    }

    @GetMapping
    public List<Jogador> listarTodos() {
        return jogadorService.listarJogadores();
    }

    @GetMapping("/{nome}")
    public List<Jogador> buscarPorNome(@PathVariable String nome) {
        return jogadorService
                .listarJogadores()
                .stream()
                .filter(j -> j.nome().equalsIgnoreCase(nome))
                .toList();
    }

    @PostMapping
    public Jogador cadastrarJogador(@RequestBody Jogador jogador)
            throws com.fasterxml.jackson.core.JsonProcessingException {
        return jogadorService.registrarJogador(jogador);
    }

    @PutMapping("/{email}")
    public Jogador atualizarJogador(@PathVariable String email, @RequestBody Jogador jogadorAtualizado) {
        if (!email.equalsIgnoreCase(jogadorAtualizado.email())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "O e-mail no caminho e no corpo não coincidem");
        }

        return jogadorService.atualizarJogador(jogadorAtualizado);
    }

    @DeleteMapping("/{email}")
    public void deletarJogador(@PathVariable String email) {
        var jogadores = jogadorService.listarJogadores();
        var existe = jogadores.stream()
            .filter(j -> j.email().equalsIgnoreCase(email))
            .findFirst()
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND, "Jogador com e-mail " + email + " não encontrado."));
    
        jogadorService.excluirJogador(email);
    }
    
}
