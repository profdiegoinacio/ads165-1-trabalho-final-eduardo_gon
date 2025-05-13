package br.com.eduardogon.cadastro_jogadores.exception;

public class GrupoCodinomeIndisponivelException extends IllegalArgumentException {
  public GrupoCodinomeIndisponivelException() {
    super("Não há codinomes disponíveis para o grupo selecionado.");
  }

}
