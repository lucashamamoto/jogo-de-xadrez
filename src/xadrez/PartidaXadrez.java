package xadrez;

import tabuleiro.Peca;
import tabuleiro.Posicao;
import tabuleiro.Tabuleiro;
import xadrez.pecas.Rei;
import xadrez.pecas.Torre;

public class PartidaXadrez {

	private Tabuleiro tabuleiro;

	public PartidaXadrez() {
		tabuleiro = new Tabuleiro(8, 8);
		initialSetup();
	}

	public PecaDeXadrez[][] getPecas() {
		PecaDeXadrez[][] mat = new PecaDeXadrez[tabuleiro.getLinhas()][tabuleiro.getColunas()];
		for (int i = 0; i < tabuleiro.getLinhas(); i++) {
			for (int j = 0; j < tabuleiro.getColunas(); j++) {
				mat[i][j] = (PecaDeXadrez) tabuleiro.peca(i, j);
			}
		}
		return mat;
	}

	public boolean[][] movimentosPossiveis(XadrezPosicao origemPosicao) {
		Posicao posicao = origemPosicao.toPosicao();
		validaPosicaoOrigem(posicao);
		return tabuleiro.peca(posicao).movimentosPossiveis();
	}

	public PecaDeXadrez performMovimentoXadrez(XadrezPosicao origemPosicao, XadrezPosicao destinoPosicao) {
		Posicao origem = origemPosicao.toPosicao();
		Posicao destino = destinoPosicao.toPosicao();
		validaPosicaoOrigem(origem);
		validaPosicaoDestino(origem, destino);
		Peca capturaPeca = movimento(origem, destino);
		return (PecaDeXadrez) capturaPeca;
	}

	private Peca movimento(Posicao origem, Posicao destino) {
		Peca p = tabuleiro.removePeca(origem);
		Peca caputaPeca = tabuleiro.removePeca(destino);
		tabuleiro.colocarPeca(p, destino);
		return caputaPeca;
	}

	private void validaPosicaoOrigem(Posicao posicao) {
		if (!tabuleiro.temUmaPeca(posicao)) {
			throw new XadrezExcecao("Nao tem peca na posicao de origem.");
		}
		if (!tabuleiro.peca(posicao).ehPossivelMoverAPeca()) {
			throw new XadrezExcecao("Nao e possivel mexer a peca escolhida.");

		}
	}

	private void validaPosicaoDestino(Posicao origem, Posicao destino) {
		if (!tabuleiro.peca(origem).movimentoPossivel(destino)) {
			throw new XadrezExcecao("A peca nao pode ser movida ao destino escolhido");
		}
	}

	private void colocarNovaPeca(char coluna, int linha, PecaDeXadrez peca) {
		tabuleiro.colocarPeca(peca, new XadrezPosicao(coluna, linha).toPosicao());
	}

	private void initialSetup() {
		colocarNovaPeca('c', 1, new Torre(tabuleiro, Cor.WHITE));
		colocarNovaPeca('c', 2, new Torre(tabuleiro, Cor.WHITE));
		colocarNovaPeca('d', 2, new Torre(tabuleiro, Cor.WHITE));
		colocarNovaPeca('e', 2, new Torre(tabuleiro, Cor.WHITE));
		colocarNovaPeca('e', 1, new Torre(tabuleiro, Cor.WHITE));
		colocarNovaPeca('d', 1, new Rei(tabuleiro, Cor.WHITE));

		colocarNovaPeca('c', 7, new Torre(tabuleiro, Cor.BLACK));
		colocarNovaPeca('c', 8, new Torre(tabuleiro, Cor.BLACK));
		colocarNovaPeca('d', 7, new Torre(tabuleiro, Cor.BLACK));
		colocarNovaPeca('e', 7, new Torre(tabuleiro, Cor.BLACK));
		colocarNovaPeca('e', 8, new Torre(tabuleiro, Cor.BLACK));
		colocarNovaPeca('d', 8, new Rei(tabuleiro, Cor.BLACK));

	}
}
