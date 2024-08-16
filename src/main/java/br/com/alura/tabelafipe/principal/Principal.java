package br.com.alura.tabelafipe.principal;

import br.com.alura.tabelafipe.model.Dados;
import br.com.alura.tabelafipe.model.Modelos;
import br.com.alura.tabelafipe.model.Veiculo;
import br.com.alura.tabelafipe.service.ConsumoApi;
import br.com.alura.tabelafipe.service.ConverteDados;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class Principal {
    Scanner reading = new Scanner(System.in);
    private final String URL = "https://parallelum.com.br/fipe/api/v1";
    private ConsumoApi consumo = new ConsumoApi();
    private ConverteDados conversor = new ConverteDados();

    public void exibeMenu(){
        var menu = """
                OPÇÕES
                Carro
                Moto
                Caminhão
                
                digite uma das opções para consultar:
                """;

        System.out.println(menu);
        var opcao = reading.nextLine();
        String endereco;

        if(opcao.toLowerCase().contains("car")){
            endereco = URL + "/carros/marcas";
        }else if(opcao.toLowerCase().contains("mot")){
            endereco = URL + "/motos/marcas";
        }else {
            endereco = URL + "/caminhoes/marcas";
        }

        var json = consumo.obterDados(endereco);
        System.out.println(json);
        var marcas = conversor.obterLista(json, Dados.class);
       marcas.stream()
               .sorted(Comparator.comparing(Dados::codigo)) // classfifica de acordo com os codigods
               .forEach(System.out::println);

        System.out.println("QUAL MARCA VOCÊ QUER CONSULTAR (DIGITE O CÓDIGO) ?");

        var codigoMarca = reading.nextLine();
        endereco += "/" + codigoMarca + "/modelos";

        json= consumo.obterDados(endereco);

        var modeloLista = conversor.obterDados(json, Modelos.class);
        modeloLista.modelos().stream()
                .sorted(Comparator.comparing(Dados::codigo))
                .forEach(System.out::println);

        System.out.println("\n Digite um trecho do nome do carro a ser buscado: ");

        var nomeVeiculo = reading.nextLine();

        List<Dados> modelosFiltrados = modeloLista.modelos().stream()
                .filter(m -> m.nome().toLowerCase().contains(nomeVeiculo.toLowerCase()))
                        .collect(Collectors.toList());
        System.out.println("\n Modelos filtrados: ");
        modelosFiltrados.forEach(System.out::println);
        System.out.println("Digite o codigo do modelo que voce deseja buscar: ");
        var codigoModelo = reading.nextLine();

        endereco += "/" + codigoModelo + "/anos";
        json = consumo.obterDados(endereco);
        List<Dados> anos = conversor.obterLista(json, Dados.class);
        List<Veiculo> veiculos = new ArrayList<>();

        for (int i = 0; i < anos.size(); i++) {
            var enderecoAnos = endereco + "/" + anos.get(i).codigo();
            json = consumo.obterDados(enderecoAnos);
            Veiculo veiculo = conversor.obterDados(json, Veiculo.class);
            veiculos.add(veiculo);
        }
        System.out.println("Todos os veiculos filtrados: " );
        veiculos.forEach(System.out::println);


    }
}
