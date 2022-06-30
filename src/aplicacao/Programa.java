package aplicacao;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

import xadrez.PartidaXadrez;
import xadrez.PecaDeXadrez;
import xadrez.PosicaoXadrez;
import xadrez.XadrezExcecao;

public class Programa {

	public static void main(String[] args) {

		Scanner sc = new Scanner(System.in);
		PartidaXadrez partidaXadrez = new PartidaXadrez();
		List<PecaDeXadrez> capturado = new ArrayList<>();

		while (!partidaXadrez.getCheckMate()) {
			try {
				UI.clearScreen();
				UI.printPartida(partidaXadrez, capturado);
				System.out.println();
				System.out.print("Origem: ");
				PosicaoXadrez origem = UI.leiaPosicaoXadrez(sc);

				boolean[][] movimentosPossiveis = partidaXadrez.movimentosPossiveis(origem);
				UI.clearScreen();
				UI.printTabuleiro(partidaXadrez.getPecas(), movimentosPossiveis);
				System.out.println();
				System.out.print("Destino: ");
				PosicaoXadrez destino = UI.leiaPosicaoXadrez(sc);

				PecaDeXadrez caputaPeca = partidaXadrez.performMovimentoXadrez(origem, destino);
				if (caputaPeca != null) {
					capturado.add(caputaPeca);
				}

				if (partidaXadrez.getPromovido() != null) {
					System.out.print("Digite para qual o peao sera promovido (B/C/T/Q): ");
					String type = sc.nextLine().toUpperCase();
					while (!type.equals("B") && !type.equals("C") && !type.equals("T") && !type.equals("Q")) {
						System.out.print("Valor invalido! Digite para qual o peao sera promovido (B/C/T/Q): ");
						type = sc.nextLine().toUpperCase();

					}
					partidaXadrez.substituirPecaPromovida(type);
				}
			} catch (XadrezExcecao e) {
				System.out.print(e.getMessage());
				sc.nextLine();
			} catch (InputMismatchException e) {
				System.out.print(e.getMessage());
				sc.nextLine();
			}

		}
		UI.clearScreen();
		UI.printPartida(partidaXadrez, capturado);

	}

}
