package mama.pluto.dataAbstraction;

import com.github.mmauro94.siopeDownloader.datastruct.anagrafiche.Anagrafiche;
import com.github.mmauro94.siopeDownloader.datastruct.anagrafiche.Comune;
import com.github.mmauro94.siopeDownloader.datastruct.anagrafiche.Ente;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class DataUtils {

    public static List<Ente> getEntiFromComune(@NotNull Anagrafiche anagrafiche, @NotNull Comune comune){
        List<Ente> ris = new ArrayList<>();
        for(Ente ente: anagrafiche.getEnti()){
            if (ente.getComune().equals(comune)){
                ris.add(ente);
            }
        }
        return ris;
    }

    public static List<Ente> searchEnte(@NotNull Anagrafiche anagrafiche, @NotNull String query){

        String queryRegex = ".*" + query.toLowerCase() + ".*";
        List<Ente> ris = new ArrayList<>();
        for(Ente ente: anagrafiche.getEnti()){
            if(ente.getNome().toLowerCase().matches(queryRegex)) {
                ris.add(ente);
            }
        }
        Collections.sort(ris, new Comparator<Ente>() {
            @Override
            public int compare(Ente ente1, Ente ente2) {
                String name1 = ente1.getNome();
                String name2 = ente2.getNome();
                if(name1.length() < name2.length()){
                    return -1;
                }
                else if(name1.length() > name2.length()){
                    return 1;
                }
                else{
                    return name1.compareTo(name2);
                }
            }
        });
        return ris;
    }

    public static List<Ente> getRegioni(@NotNull Anagrafiche anagrafiche){
        List<Ente> ris = new ArrayList<>();
        for (Ente ente: anagrafiche.getEnti()){
            if(ente.getSottocomparto().getCodice().equals("REGIONE")){
                ris.add(ente);
            }
        }
        return ris;
    }
    public static List<Ente> getProvince(@NotNull Anagrafiche anagrafiche){
        List<Ente> ris = new ArrayList<>();
        for (Ente ente: anagrafiche.getEnti()){
            if(ente.getSottocomparto().getCodice().equals("PROVINCIA")){
                ris.add(ente);
            }
        }
        return ris;
    }
    public static List<Ente> getComuni(@NotNull Anagrafiche anagrafiche){
        List<Ente> ris = new ArrayList<>();
        for (Ente ente: anagrafiche.getEnti()){
            if(ente.getSottocomparto().getCodice().equals("COMUNE")){
                ris.add(ente);
            }
        }
        return ris;
    }

}
