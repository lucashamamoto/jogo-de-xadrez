package xadrez;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import tabuleiro.Peca;
import tabuleiro.Posicao;
import tabuleiro.Tabuleiro;
import xadrez.pecas.Bispo;
import xadrez.pecas.Cavalo;
import xadrez.pecas.Peao;
import xadrez.pecas.Rainha;
import xadrez.pecas.Rei;
import xadrez.pecas.Torre;

public class PartidaXadrez {

	private int turno;
	private Cor jogadorAtual;
	private Tabuleiro tabuleiro;
	private boolean check;
	private boolean checkMate;
	private PecaDeXadrez enPassantVuneravel;

	private List<Peca> pecasNoTabuleiro = new ArrayList<>();
	private List<Peca> pecasCapturadas = new ArrayList<>();

	public PartidaXadrez() {
		tabuleiro = new Tabuleiro(8, 8);
		turno = 1;
		jogadorAtual = Cor.WHITE;
		check = false;
		initialSetup();
	}

	public int getTurno() {
		return turno;
	}

	public Cor getJogadorAtual() {
		return jogadorAtual;
	}

	public boolean getCheck() {
		return check;
	}

	public boolean getCheckMate() {
		return checkMate;
	}

	public PecaDeXadrez getEnPassantVuneravel() {
		return enPassantVuneravel;
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

	public boolean[][] movimentosPossiveis(PosicaoXadrez origemPosicao) {
		Posicao posicao = origemPosicao.toPosicao();
		validaPosicaoOrigem(posicao);
		return tabuleiro.peca(posicao).movimentosPossiveis();
	}

	public PecaDeXadrez performMovimentoXadrez(PosicaoXadrez origemPosicao, PosicaoXadrez destinoPosicao) {
		Posicao origem = origemPosicao.toPosicao();
		Posicao destino = destinoPosicao.toPosicao();
		validaPosicaoOrigem(origem);
		validaPosicaoDestino(origem, destino);
		Peca pecaCapturada = movimento(origem, destino);

		if (testeCheck(jogadorAtual)) {
			desfazerMovimento(origem, destino, pecaCapturada);
			throw new XadrezExcecao("Voce nao pode se colocar em check");
		}
		PecaDeXadrez pecaMovida = (PecaDeXadrez) tabuleiro.peca(destino);

		check = (testeCheck(oponente(jogadorAtual))) ? true : false;

		if (testeCheckMate(oponente(jogadorAtual))) {
			checkMate = true;
		} else {
			proximoTurno();
		}

		// movimento especial en passant
		if (pecaMovida instanceof Peao
				&& (destino.getLinha() == origem.getLinha() - 2 || (destino.getLinha() == origem.getLinha() + 2))) {
			enPassantVuneravel = pecaMovida;
		} else {
			enPassantVuneravel = null;
		}

		return (PecaDeXadrez) pecaCapturada;
	}

	private Peca movimento(Posicao origem, Posicao destino) {
		PecaDeXadrez p = (PecaDeXadrez) tabuleiro.removePeca(origem);
		p.aumentoContagemMovimento();
		Peca pecaCapturada = tabuleiro.removePeca(destino);
		tabuleiro.colocarPeca(p, destino);

		if (pecaCapturada != null) {
			pecasNoTabuleiro.remove(pecaCapturada);
			pecasCapturadas.add(pecaCapturada);
		}

		// movimento especial Roque lado do rei Torre
		if (p instanceof Rei && destino.getColuna() == origem.getColuna() + 2) {
			Posicao origemT = new Posicao(origem.getLinha(), origem.getColuna() + 3);
			Posicao destinoT = new Posicao(origem.getLinha(), origem.getColuna() + 1);
			PecaDeXadrez torre = (PecaDeXadrez) tabuleiro.removePeca(origemT);
			tabuleiro.colocarPeca(torre, destinoT);
			torre.aumentoContagemMovimento();
		}
		// movimento especial Roque lado da rainha Torre
		if (p instanceof Rei && destino.getColuna() == origem.getColuna() - 2) {
			Posicao origemT = new Posicao(origem.getLinha(), origem.getColuna() - 4);
			Posicao destinoT = new Posicao(origem.getLinha(), origem.getColuna() - 1);
			PecaDeXadrez torre = (PecaDeXadrez) tabuleiro.removePeca(origemT);
			tabuleiro.colocarPeca(torre, destinoT);
			torre.aumentoContagemMovimento();
		}
		// movimento especial en passant
		if (p instanceof Peao) {
			if (origem.getColuna() != destino.getColuna() && pecaCapturada == null) {
				Posicao peaoPosicao;
				if (p.getCor() == Cor.WHITE) {
					peaoPosicao = new Posicao(destino.getLinha() + 1, destino.getColuna());
				} else {
					peaoPosicao = new Posicao(destino.getLinha() - 1, destino.getColuna());
				}
				pecaCapturada = tabuleiro.removePeca(peaoPosicao);
				pecasCapturadas.add(pecaCapturada);
				pecasNoTabuleiro.remove(pecaCapturada);
			}
		}
		return pecaCapturada;
	}

	private void desfazerMovimento(Posicao origem, Posicao destino, Peca pecaCapturada) {
		PecaDeXadrez p = (PecaDeXadrez) tabuleiro.removePeca(destino);
		p.reduzContagemMovimento();
		tabuleiro.colocarPeca(p, origem);

		if (pecaCapturada != null) {
			tabuleiro.colocarPeca(pecaCapturada, destino);
			pecasCapturadas.remove(pecaCapturada);
			pecasNoTabuleiro.add(pecaCapturada);
		}

		// movimento especial Roque lado do rei Torre
		if (p instanceof Rei && destino.getColuna() == origem.getColuna() + 2) {
			Posicao origemT = new Posicao(origem.getLinha(), origem.getColuna() + 3);
			Posicao destinoT = new Posicao(origem.getLinha(), origem.getColuna() + 1);
			PecaDeXadrez torre = (PecaDeXadrez) tabuleiro.removePeca(destinoT);
			tabuleiro.colocarPeca(torre, origemT);
			torre.reduzContagemMovimento();
			;
		}
		// movimento especial Roque lado da rainha Torre
		if (p instanceof Rei && destino.getColuna() == origem.getColuna() - 2) {
			Posicao origemT = new Posicao(origem.getLinha(), origem.getColuna() - 4);
			Posicao destinoT = new Posicao(origem.getLinha(), origem.getColuna() - 1);
			PecaDeXadrez torre = (PecaDeXadrez) tabuleiro.removePeca(destinoT);
			tabuleiro.colocarPeca(torre, origemT);
			torre.reduzContagemMovimento();
			;
		}
		// movimento especial en passant
		if (p instanceof Peao) {
			if (origem.getColuna() != destino.getColuna() && pecaCapturada == enPassantVuneravel) {
				PecaDeXadrez peao = (PecaDeXadrez)tabuleiro.removePeca(destino);
				Posicao peaoPosicao;
				if (p.getCor() == Cor.WHITE) {
					peaoPosicao = new Posicao(3, destino.getColuna());
				} else {
					peaoPosicao = new Posicao(4, destino.getColuna());
				}
				tabuleiro.colocarPeca(peao, peaoPosicao);
			}
		}
	}

	private void validaPosicaoOrigem(Posicao posicao) {
		if (!tabuleiro.temUmaPeca(posicao)) {
			throw new XadrezExcecao("Nao tem peca na posicao de origem.");
		}
		if (jogadorAtual != ((PecaDeXadrez) tabuleiro.peca(posicao)).getCor()) {
			throw new XadrezExcecao("A peca escolhida nao eh sua.");
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

	private void proximoTurno() {
		turno++;
		jogadorAtual = (jogadorAtual == Cor.WHITE) ? Cor.BLACK : Cor.WHITE;
	}

	private Cor oponente(Cor cor) {
		return (cor == cor.WHITE) ? cor.BLACK : cor.WHITE;
	}

	private PecaDeXadrez rei(Cor cor) {
		List<Peca> list = pecasNoTabuleiro.stream().filter(x -> ((PecaDeXadrez) x).getCor() == cor)
				.collect(Collectors.toList());
		for (Peca p : list) {
			if (p instanceof Rei) {
				return (PecaDeXadrez) p;
			}
		}
		throw new IllegalStateException("Não tem rei " + cor + " no tabuleiro");
	}

	private boolean testeCheck(Cor cor) {
		Posicao reiPosicao = rei(cor).getPosicaoXadrez().toPosicao();
		List<Peca> pecasOponente = pecasNoTabuleiro.stream().filter(x -> ((PecaDeXadrez) x).getCor() == oponente(cor))
				.collect(Collectors.toList());
		for (Peca p : pecasOponente) {
			boolean[][] mat = p.movimentosPossiveis();
			if (mat[reiPosicao.getLinha()][reiPosicao.getColuna()]) {
				return true;
			}
		}
		return false;
	}

	private boolean testeCheckMate(Cor cor) {
		if (!testeCheck(cor)) {
			return false;
		}
		List<Peca> list = pecasNoTabuleiro.stream().filter(x -> ((PecaDeXadrez) x).getCor() == (cor))
				.collect(Collectors.toList());
		for (Peca p : list) {
			boolean[][] mat = p.movimentosPossiveis();
			for (int i = 0; i < tabuleiro.getLinhas(); i++) {
				for (int j = 0; j < tabuleiro.getColunas(); j++) {
					if (mat[i][j]) {
						Posicao origem = ((PecaDeXadrez) p).getPosicaoXadrez().toPosicao();
						Posicao destino = new Posicao(i, j);
						Peca pecaCapturada = movimento(origem, destino);
						boolean testeCheck = testeCheck(cor);
						desfazerMovimento(origem, destino, pecaCapturada);
						if (!testeCheck) {
							return false;
						}
					}
				}
			}
		}
		return true;
	}

	private void colocarNovaPeca(char coluna, int linha, PecaDeXadrez peca) {
		tabuleiro.colocarPeca(peca, new PosicaoXadrez(coluna, linha).toPosicao());
		pecasNoTabuleiro.add(peca);
	}

	private void initialSetup() {
		colocarNovaPeca('a', 1, new Torre(tabuleiro, Cor.WHITE));
		colocarNovaPeca('b', 1, new Cavalo(tabuleiro, Cor.WHITE));
		colocarNovaPeca('c', 1, new Bispo(tabuleiro, Cor.WHITE));
		colocarNovaPeca('d', 1, new Rainha(tabuleiro, Cor.WHITE));
		colocarNovaPeca('e', 1, new Rei(tabuleiro, Cor.WHITE, this));
		colocarNovaPeca('f', 1, new Bispo(tabuleiro, Cor.WHITE));
		colocarNovaPeca('g', 1, new Cavalo(tabuleiro, Cor.WHITE));
		colocarNovaPeca('h', 1, new Torre(tabuleiro, Cor.WHITE));
		colocarNovaPeca('a', 2, new Peao(tabuleiro, Cor.WHITE, this));
		colocarNovaPeca('b', 2, new Peao(tabuleiro, Cor.WHITE, this));
		colocarNovaPeca('c', 2, new Peao(tabuleiro, Cor.WHITE, this));
		colocarNovaPeca('d', 2, new Peao(tabuleiro, Cor.WHITE, this));
		colocarNovaPeca('e', 2, new Peao(tabuleiro, Cor.WHITE, this));
		colocarNovaPeca('f', 2, new Peao(tabuleiro, Cor.WHITE, this));
		colocarNovaPeca('g', 2, new Peao(tabuleiro, Cor.WHITE, this));
		colocarNovaPeca('h', 2, new Peao(tabuleiro, Cor.WHITE, this));

		colocarNovaPeca('a', 8, new Torre(tabuleiro, Cor.BLACK));
		colocarNovaPeca('b', 8, new Cavalo(tabuleiro, Cor.BLACK));
		colocarNovaPeca('c', 8, new Bispo(tabuleiro, Cor.BLACK));
		colocarNovaPeca('d', 8, new Rainha(tabuleiro, Cor.BLACK));
		colocarNovaPeca('e', 8, new Rei(tabuleiro, Cor.BLACK, this));
		colocarNovaPeca('f', 8, new Bispo(tabuleiro, Cor.BLACK));
		colocarNovaPeca('g', 8, new Cavalo(tabuleiro, Cor.BLACK));
		colocarNovaPeca('h', 8, new Torre(tabuleiro, Cor.BLACK));
		colocarNovaPeca('a', 7, new Peao(tabuleiro, Cor.BLACK, this));
		colocarNovaPeca('b', 7, new Peao(tabuleiro, Cor.BLACK, this));
		colocarNovaPeca('c', 7, new Peao(tabuleiro, Cor.BLACK, this));
		colocarNovaPeca('d', 7, new Peao(tabuleiro, Cor.BLACK, this));
		colocarNovaPeca('e', 7, new Peao(tabuleiro, Cor.BLACK, this));
		colocarNovaPeca('f', 7, new Peao(tabuleiro, Cor.BLACK, this));
		colocarNovaPeca('g', 7, new Peao(tabuleiro, Cor.BLACK, this));
		colocarNovaPeca('h', 7, new Peao(tabuleiro, Cor.BLACK, this));

	}
}
