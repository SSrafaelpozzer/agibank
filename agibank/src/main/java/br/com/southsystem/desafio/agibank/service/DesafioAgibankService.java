package br.com.southsystem.desafio.agibank.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.nio.file.DirectoryStream;
import java.nio.file.DirectoryStream.Filter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import br.com.southsystem.desafio.agibank.model.Cliente;
import br.com.southsystem.desafio.agibank.model.ItemVenda;
import br.com.southsystem.desafio.agibank.model.Venda;
import br.com.southsystem.desafio.agibank.model.Vendedor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class DesafioAgibankService implements IDesafioAgibankService {

	@Value("${dirIn}")
	private String dirIn;
	
	@Value("${dirOut}")
	private String dirOut;
	
	@Value("${firstSplitSeparator}")
	private String firstSplitSeparator;
	
	@Value("${secondSplitSeparator}")
	private String secondSplitSeparator;
	
	@Value("${thirdSplitSeparator}")
	private String thirdSplitSeparator;
	
	@Value("${itemReplace1}")
	private String itemReplace1;
	
	@Value("${itemReplace2}")
	private String itemReplace2;
	
	@Value("${codigoVendedor}")
	private String codigoVendedor;
	
	@Value("${codigoCliente}")
	private String codigoCliente;
	
	@Value("${codigoVenda}")
	private String codigoVenda;
	
	@Override
	public Void readDirectory() {
		String inPath = System.getProperty("user.home") + dirIn;
		//WatchService watchService = FileSystems.getDefault().newWatchService();
		//Path pathInit = Paths.get(inPath);
		//pathInit.register(watchService, StandardWatchEventKinds.ENTRY_CREATE);
		//WatchKey key;
    
		do  {
		 
		    try (DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get(inPath), new Filter<Path>() {
				@Override
				public boolean accept(Path entry) throws IOException {
					return !Files.isDirectory(entry) && entry.getFileName().toString().endsWith(".dat");
				}
			})) { 
			stream.forEach(path -> {
						try {
							List<String> lines= Files.readAllLines(path, Charset.defaultCharset());
							
							Flux<String> stringFlux = Flux.just(lines).flatMap(Flux::fromIterable);
							Mono<List<Vendedor>> vendedores = stringFlux.filter(a -> a.startsWith(codigoVendedor)).map(line -> line.split(firstSplitSeparator)).map(a2 ->
							new Vendedor(Long.parseLong(a2[1]), retornaNome(a2, firstSplitSeparator, 2, 1), new BigDecimal(a2[a2.length-1]))).collect(Collectors.toList()).onErrorContinue((t,o) -> {
								t.printStackTrace();
							});
							
							Mono<List<Cliente>> clientes = stringFlux.filter(a -> a.startsWith(codigoCliente)).map(line -> line.split(firstSplitSeparator)).map(a2 ->
							new Cliente(Long.parseLong(a2[1]), retornaNome(a2, firstSplitSeparator, 2, 1), a2[a2.length-1])).collect(Collectors.toList()).onErrorContinue((t,o) -> {
								t.printStackTrace();
							});
							
							Mono<List<Venda>> vendas = stringFlux.filter(a -> a.startsWith("003")).map(line -> line.split(firstSplitSeparator)).map(a2 -> {
								Stream<String> subStream = Pattern.compile(secondSplitSeparator).splitAsStream( a2[2].replace(itemReplace1, "").replace(itemReplace2, "") );
								List<ItemVenda> itensVenda = subStream.map(sub -> sub.split(thirdSplitSeparator)).map(sub2 -> 
								new ItemVenda(Long.parseLong(sub2[0]), Long.parseLong(sub2[1]), new BigDecimal(sub2[2]))
								).collect(Collectors.toList());
								return new Venda(Long.parseLong(a2[1]), retornaNome(a2, firstSplitSeparator, 3, 0), itensVenda);
							}).collect(Collectors.toList()).onErrorContinue((t,o) -> {
								t.printStackTrace();								
							});
							File outputFile = createOutputFile(dirOut, path);
							printFile(outputFile, vendedores, clientes, vendas);
							path.toFile().delete();
						} catch (Exception e) {
							e.printStackTrace();
							System.out.println("Arquivo: "+path+" com problema");
						}
					});
			stream.close();
		    } catch (IOException e1) {
				e1.printStackTrace();
			}
		} while (Boolean.TRUE);
		return null;
	}
	
	private static String retornaNome(String[] splitLine, String split, int posicao, int posicao2) {
		if (splitLine.length > 4) {
			for (int i = posicao+1; i < splitLine.length-posicao2; i++) {
				splitLine[posicao] = splitLine[posicao]+split+splitLine[i];
			}
		}
		return splitLine[posicao];
	}
	
	public static File createOutputFile(String dirOut, Path currentFile) throws Exception{
		File pathDir = new File(System.getProperty("user.home")+dirOut);
		
		if (!pathDir.exists() && !pathDir.mkdirs()) {
			throw new Exception("Caminho não existe ou não pode ser criado: " + dirOut);
		} 
		String currentFileName = currentFile.getFileName().toFile().getName();
		int lastIndex = currentFileName.lastIndexOf(".dat");
		String outputFileName = pathDir.getAbsolutePath() + "//" + currentFileName.substring(0, lastIndex) + ".done.dat";
		return new File(outputFileName);
	}
	
	public static void printFile(File outputFile, Mono<List<Vendedor>> vendedores, Mono<List<Cliente>> clientes, Mono<List<Venda>> vendas){
		try {
			
			PrintWriter output = new PrintWriter(outputFile);
			clientes.subscribe(c -> output.println("Quantidade de clientes no arquivo de entrada: " + c.size()));
			vendedores.subscribe(v -> output.println("Quantidade de vendedores no arquivo de entrada: " + v.size()));
			vendas.filter(v -> v.size()>0).subscribe(v -> {
				v.sort((Venda v1, Venda v2) -> v1.getTotal().compareTo(v2.getTotal()));
				output.println("ID da venda mais cara: " + v.get(v.size()-1).getSaleId());
				output.println("O pior vendedor: " + v.get(0).getVendedor());
				
			});
			output.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
	}
	
}
