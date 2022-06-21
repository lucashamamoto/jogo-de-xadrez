package aplicacao;

import java.util.InputMismatchException;
import java.util.Scanner;

import xadrez.PartidaXadrez;
import xadrez.PecaDeXadrez;
import xadrez.XadrezExcecao;
import xadrez.XadrezPosicao;

public class Programa {

	public static void main(String[] args) {
		
		Scanner sc = new Scanner(System.in);
		PartidaXadrez partidaXadrez = new PartidaXadrez();
		
		while (true) {
			try {
				UI.clearScreen();
				UI.printTabuleiro(partidaXadrez.getPecas());
				System.out.println();
				System.out.print("Origem: ");
				XadrezPosicao origem = UI.leiaXadrezPosicao(sc);
				
				System.out.println();
				System.out.print("Destino: ");
				XadrezPosicao destino = UI.leiaXadrezPosicao(sc);
				
				PecaDeXadrez caputaPeca = partidaXadrez.performMovimentoXadrez(origem, destino);
			}
			catch (XadrezExcecao e) {
				System.out.print(e.getMessage());
				sc.nextLine();
			}
			catch (InputMismatchException e) {
				System.out.print(e.getMessage());
				sc.nextLine();
			}

		}
		
		
	}

}
