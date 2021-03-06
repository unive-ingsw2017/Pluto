package mama.pluto.dataAbstraction;

import com.github.mmauro94.siopeDownloader.datastruct.anagrafiche.Anagrafiche;
import com.github.mmauro94.siopeDownloader.datastruct.anagrafiche.CodiceGestionale;
import com.github.mmauro94.siopeDownloader.datastruct.anagrafiche.CodiceGestionaleEntrate;
import com.github.mmauro94.siopeDownloader.datastruct.anagrafiche.CodiceGestionaleUscite;
import com.github.mmauro94.siopeDownloader.datastruct.anagrafiche.Comparto;
import com.github.mmauro94.siopeDownloader.datastruct.anagrafiche.Comune;
import com.github.mmauro94.siopeDownloader.datastruct.anagrafiche.Ente;
import com.github.mmauro94.siopeDownloader.datastruct.anagrafiche.Provincia;
import com.github.mmauro94.siopeDownloader.datastruct.anagrafiche.Regione;
import com.github.mmauro94.siopeDownloader.datastruct.anagrafiche.RipartizioneGeografica;
import com.github.mmauro94.siopeDownloader.datastruct.anagrafiche.Sottocomparto;

import org.jetbrains.annotations.NotNull;

import mama.pluto.utils.DoubleMap;

public class AnagraficheExtended {

    private final Anagrafiche anagrafiche;
    private final DoubleMap<Long, CodiceGestionale> codiceGestionaleIdMap;
    private final DoubleMap<Long, Ente> enteIdMap;

    public AnagraficheExtended(Anagrafiche anagrafiche, DoubleMap<Long, CodiceGestionale> codiceGestionaleIdMap, DoubleMap<Long, Ente> enteIdMap) {
        if (anagrafiche.getCodiciGestionaliEntrate().size() + anagrafiche.getCodiciGestionaliUscite().size() != codiceGestionaleIdMap.size()) {
            throw new IllegalArgumentException();
        }
        if (anagrafiche.getEnti().size() != enteIdMap.size()) {
            throw new IllegalArgumentException();
        }
        for (Ente ente : anagrafiche.getEnti()) {
            if (!enteIdMap.containsK2(ente)) {
                throw new IllegalArgumentException();
            }
        }
        for (CodiceGestionaleEntrate cg : anagrafiche.getCodiciGestionaliEntrate()) {
            if (!codiceGestionaleIdMap.containsK2(cg)) {
                throw new IllegalArgumentException();
            }
        }
        for (CodiceGestionaleEntrate cg : anagrafiche.getCodiciGestionaliEntrate()) {
            if (!codiceGestionaleIdMap.containsK2(cg)) {
                throw new IllegalArgumentException();
            }
        }
        this.anagrafiche = anagrafiche;
        this.codiceGestionaleIdMap = codiceGestionaleIdMap;
        this.enteIdMap = enteIdMap;
    }

    @NotNull
    public Anagrafiche getAnagrafiche() {
        return anagrafiche;
    }

    public Comparto.Map getComparti() {
        return anagrafiche.getComparti();
    }

    public Sottocomparto.Map getSottocomparti() {
        return anagrafiche.getSottocomparti();
    }

    public RipartizioneGeografica.Map getRipartizioniGeografiche() {
        return anagrafiche.getRipartizioniGeografiche();
    }

    public Regione.Map getRegioni() {
        return anagrafiche.getRegioni();
    }

    public Provincia.Map getProvincie() {
        return anagrafiche.getProvincie();
    }

    public Comune.Map getComuni() {
        return anagrafiche.getComuni();
    }

    public Ente.Map getEnti() {
        return anagrafiche.getEnti();
    }

    public CodiceGestionaleEntrate.Map getCodiciGestionaliEntrate() {
        return anagrafiche.getCodiciGestionaliEntrate();
    }

    public CodiceGestionaleUscite.Map getCodiciGestionaliUscite() {
        return anagrafiche.getCodiciGestionaliUscite();
    }

    public long getIdCodiceGestionale(@NotNull CodiceGestionale codiceGestionale) {
        Long ret = codiceGestionaleIdMap.getK1(codiceGestionale);
        if (ret == null) {
            throw new IllegalArgumentException("Unknown codice gestionale");
        } else {
            return ret;
        }
    }

    public long getIdEnte(@NotNull Ente ente) {
        Long ret = enteIdMap.getK1(ente);
        if (ret == null) {
            throw new IllegalArgumentException("Unknown ente");
        } else {
            return ret;
        }
    }

    public Ente getEnteById(long idEnte) {
        Ente ret = enteIdMap.getK2(idEnte);
        if (ret == null) {
            throw new IllegalArgumentException("Unknown ente id");
        } else {
            return ret;
        }
    }

    public CodiceGestionale getCodiceGestionaleById(long idCodiceGestionale) {
        CodiceGestionale ret = codiceGestionaleIdMap.getK2(idCodiceGestionale);
        if (ret == null) {
            throw new IllegalArgumentException("Unknown codice gestionale");
        } else {
            return ret;
        }
    }

    public static class Builder {
        private final DoubleMap<Long, CodiceGestionale> codiceGestionaleIdMap = new DoubleMap<>();
        private final DoubleMap<Long, Ente> enteIdMap = new DoubleMap<>();

        public void put(long id, Ente ente) {
            enteIdMap.put(id, ente);
        }

        public void put(long id, CodiceGestionale codiceGestionale) {
            codiceGestionaleIdMap.put(id, codiceGestionale);
        }

        public AnagraficheExtended build(Anagrafiche anagrafiche) {
            return new AnagraficheExtended(anagrafiche, codiceGestionaleIdMap, enteIdMap);
        }
    }
}
