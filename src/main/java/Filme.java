import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class Filme {

    static String url = "https://www.imdb.com/chart/bottom";
    private Double nota;
    private String moreInfoPath;
    private List<String> diretores = new ArrayList<String>();
    private List<String> atores = new ArrayList<String>();
    private List<String> comentarios = new ArrayList<String>();
    private List<String> notasComentarios = new ArrayList<>();
    private List<String> nomesOriginais = new ArrayList<>();

    public Filme(String nome, Double nota) {
        this.nota = nota;
    }

    public Filme() {

    }

    public List<String> getDiretores() {
        return diretores;
    }

    public void setDiretores(List<String> diretores) {
        this.diretores = diretores;
    }
    public String getMoreInfoPath() {
        return moreInfoPath;
    }

    public void setMoreInfoPath(String moreInfoPath) {
        this.moreInfoPath = moreInfoPath;
    }

    public Double getNota() {
        return nota;
    }

    public void setNota(Double nota) {
        this.nota = nota;
    }

    public List<String> getAtores() {
        return atores;
    }

    public void setAtores(List<String> atores) {
        this.atores = atores;
    }

    public List<String> getComentarios() {
        return comentarios;
    }

    public void setComentarios(List<String> comentarios) {
        this.comentarios = comentarios;
    }

    public List<String> getNotasComentarios() {
        return notasComentarios;
    }

    public void setNotaComentario(List<String> notaComentario) {
        this.notasComentarios = notaComentario;
    }

    public List<String> getNomesOriginais() {
        return nomesOriginais;
    }

    public void setNomeOriginal(List<String> nomeOriginal) {
        this.nomesOriginais = nomeOriginal;
    }

    public void findElements() throws IOException, InterruptedException {

        System.out.println("Tentando conectar ao site...");
        //Comando que conecta ao site que deseja verificar.
        Document doc = Jsoup.connect(url).get();
        System.out.println("Conectado ao site.");

        //Trecho de codigo que conecta aos elementos da primeira pagina, para pegar o elemento nota, que era o mais facil.
        Element table = doc.getElementsByClass("chart full-width").first();
        Element tbody = table.getElementsByTag("tbody").first();
        List<Element> elements = tbody.getElementsByTag("tr");
        List<Filme> filmes = new ArrayList<Filme>();
        //ForEach que percorre os elementos e pega o path(Complemento de link para entrar em outras paginas) e a nota.
        elements.forEach(e -> {
            List<Element> attributes = e.getElementsByTag("td");
            Element filmNameElement = attributes.get(1);
            Double nota = Double.parseDouble(attributes.get(2).text());
            String path = filmNameElement.getElementsByTag("a").first().attr("href");
            Filme filme = new Filme();
            filme.setMoreInfoPath(path);
            filme.setNota(nota);
            filmes.add(filme);
        });

        //Lista que pega as notas apenas dos 10 filmes desejados.
        List<Filme> filmesFiltrados = filmes.stream().sorted(Comparator.comparing(e-> e.getNota())).limit(10).collect(Collectors.toList());
        //Chamada dos metodos com paramentros path.
        Thread.sleep(2000);
        System.out.println("Encontrando Elementos...");
        Thread.sleep(2000);
        System.out.println("Aguarde alguns instantes...");
        filmesFiltrados.forEach(e->{
            try {
                extractOriginalName(e.getMoreInfoPath(),e);

            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
            try {
                extractDirector(e.getMoreInfoPath(),e);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
            try {
                extractAtores(e.getMoreInfoPath(),e);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
            try {
                extractComentarios(e.getMoreInfoPath(),e);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });
        System.out.println("Elementos encontrados.");

        System.out.println("--------------------------------------------------------------");

        result(filmesFiltrados);
    }

    private static void result(List<Filme> filmesFiltrados) {

        //Esse metodo é apenas um void para organizar os resultados que serão mostrados no console.
        for (int i = filmesFiltrados.size() - 1; i >= 0; i--) {
            System.out.println("Nome Original: " + filmesFiltrados.get(i).getNomesOriginais().get(filmesFiltrados.get(i).getNomesOriginais().size()-1).toString().substring(16));
            filmesFiltrados.get(i).getNomesOriginais().stream().limit(filmesFiltrados.get(i).getNomesOriginais().size()-1).map(s -> s + ", ").forEach(System.out::print);

            System.out.println("Nota: " + filmesFiltrados.get(i).getNota());

            System.out.println("Diretores: " + filmesFiltrados.get(i).getDiretores().get(filmesFiltrados.get(i).getDiretores().size()-1));
            filmesFiltrados.get(i).getDiretores().stream().limit(filmesFiltrados.get(i).getDiretores().size()-1).map(s -> s + ", ");

            System.out.println("\nAtores: " +  filmesFiltrados.get(i).getAtores().get(filmesFiltrados.get(i).getAtores().size()-1));
            filmesFiltrados.get(i).getAtores().stream().limit(filmesFiltrados.get(i).getAtores().size()-1).map(s -> s + ", ").forEach(System.out::print);

            System.out.println("\nNotas dos comentarios: " + filmesFiltrados.get(i).getNotasComentarios().get(0).toString());

            System.out.println("Comentarios: " + filmesFiltrados.get(i).getComentarios().get(0).toString());

            System.out.println("--------------------------------------------------------------");
        }
    }
    //Os metodos Extract a seguir, vão simplismente entrar nas paginas das request e extrair os dados principaisdo filme
    //como Nome em ingles, equipe de direção
    public static void extractOriginalName(String path, Filme filme) throws IOException {
        Document doc = Jsoup.connect("https://www.imdb.com/"+ path).get();
        List<Element> elements = new ArrayList<Element>();
        elements = doc.getElementsByClass("sc-dae4a1bc-0 gwBsXc");
        elements.forEach(e->{
                    filme.getNomesOriginais().add(e.text());
                }
        );
    }
    public static void extractDirector(String path, Filme filme) throws IOException {
        Document doc = Jsoup.connect("https://www.imdb.com/"+ path).get();
        List<Element> elements = new ArrayList<Element>();
        elements = doc.select("div:nth-child(1) > div > ul > li:nth-child(1) > div > ul > li > a");
        elements.forEach(e->{
                    filme.getDiretores().add(e.text());
                }
        );
    }
    public static void extractAtores(String path, Filme filme) throws IOException {
        Document doc = Jsoup.connect("https://www.imdb.com/"+ path).get();
        List<Element> elements = new ArrayList<Element>();
        elements = doc.select("div:nth-child(1) > div > ul > li:nth-child(3) > div > ul > li > a");
        elements.forEach(e->{
                    filme.getAtores().add(e.text());
                }
        );
    }
    public static void extractComentarios(String path, Filme filme) throws IOException {
        path = path.substring(0, 17);
        Document doc = Jsoup.connect("https://www.imdb.com/"+ path +"reviews?sort=userRating&dir=desc&ratingFilter=0").get();
        List<Element> elements = new ArrayList<Element>();
        List<Element> comments = new ArrayList<Element>();
        elements = doc.getElementsByClass("rating-other-user-rating");
        comments = doc.getElementsByClass("text show-more__control");
        elements.forEach(e->{
                    filme.getNotasComentarios().add(e.text());
                }
        );
        comments.forEach(e->{

            filme.getComentarios().add(e.text());
        });
    }
}
