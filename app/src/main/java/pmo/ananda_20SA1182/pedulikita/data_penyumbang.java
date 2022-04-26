package pmo.ananda_20SA1182.pedulikita;
public class data_penyumbang {

    private String nama;
    private String key;
    private String bank;
    private String nope;
    private String email;
    private String kategori;
    private String jeniskelamin;
    private String gol1;
    private String gol2;
    private String gambar;



    public String getJeniskelamin() {
        return jeniskelamin;
    }
    public void setJeniskelamin(String jeniskelamin) {
        this.jeniskelamin = jeniskelamin;
    }

    public String getNama() {
        return nama;
    }
    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getKey() {
        return key;
    }
    public void setKey(String key) {
        this.key = key;
    }

    public String getBank() {
        return bank;
    }
    public void setBank(String bank) {
        this.bank = bank;
    }

    public String getNope() {
        return nope;
    }
    public void setNope(String nope) {
        this.nope = nope;
    }

    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    public String getKategori() { return kategori; }
    public void setKategori(String kategori) {
        this.kategori = kategori;
    }

    public void setGambar(String gambar) {
        this.gambar = gambar;
    }
    public String getGambar() {
        return gambar;
    }

    public String getgol1() {
        return gol1;
    }
    public void setgol1(String Sosial) {
        gol1 = Sosial;
    }

    public String getgol2() {
        return gol2;
    }
    public void setgol2(String Keagamaan) {
        gol2 = Keagamaan;
    }

    public data_penyumbang() {
    }

    public data_penyumbang(String nama, String bank, String nope, String email, String kategori, String jeniskelamin, String gambar) {

        this.nama = nama;
        this.bank = bank;
        this.nope = nope;
        this.email = email;
        this.kategori = kategori;
        this.jeniskelamin = jeniskelamin;
        this.gambar = gambar;
    }
}