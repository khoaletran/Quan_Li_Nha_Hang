package entity;

import dao.PhanTramGiaBanDAO;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class Mon {

    private String maMon;
    private String tenMon;
    private String moTa;
    private String hinhAnh;
    private double giaGoc;
    private int soLuong;
    private LoaiMon loaiMon;

    // =====================================================
    //  PTGB CACHE – chống gọi DB 100 lần mỗi giây
    // =====================================================
    private static final Map<String, Integer> CACHE_PT_MON = new HashMap<>();
    private static final Map<String, Integer> CACHE_PT_LOAIMON = new HashMap<>();

    // Cache theo ngày khi tính PTGB tại thời điểm lập hóa đơn
    private static final Map<String, Integer> CACHE_PT_MON_THEO_NGAY = new HashMap<>();
    private static final Map<String, Integer> CACHE_PT_LOAIMON_THEO_NGAY = new HashMap<>();


    public Mon() {}

    public Mon(String maMon, String tenMon, String moTa, String hinhAnh, double giaGoc, int soLuong, LoaiMon loaiMon) {
        this.maMon = maMon;
        this.tenMon = tenMon;
        this.moTa = moTa;
        this.hinhAnh = hinhAnh;
        this.giaGoc = giaGoc;
        this.soLuong = soLuong;
        this.loaiMon = loaiMon;
    }

    // =====================================================
    // 1. PTGB HIỆN TẠI (không theo ngày)
    // =====================================================
    public int getPhanTramGiaBanHienTai() {

        // Ưu tiên lấy từ cache
        if (CACHE_PT_MON.containsKey(maMon))
            return CACHE_PT_MON.get(maMon);

        if (CACHE_PT_LOAIMON.containsKey(loaiMon.getMaLoaiMon()))
            return CACHE_PT_LOAIMON.get(loaiMon.getMaLoaiMon());

        // Không có → gọi DB 1 lần duy nhất
        var ptMon = PhanTramGiaBanDAO.getLatestForMon(maMon);
        if (ptMon != null) {
            CACHE_PT_MON.put(maMon, ptMon.getPhanTramLoi());
            return ptMon.getPhanTramLoi();
        }

        var ptLoai = PhanTramGiaBanDAO.getLatestForLoaiMon(loaiMon.getMaLoaiMon());
        if (ptLoai != null) {
            CACHE_PT_LOAIMON.put(loaiMon.getMaLoaiMon(), ptLoai.getPhanTramLoi());
            return ptLoai.getPhanTramLoi();
        }

        return 0;
    }

    public double getGiaBan() {
        int pt = getPhanTramGiaBanHienTai();
        return giaGoc * (1 + pt / 100.0);
    }


    // =====================================================
    // 2. PTGB THEO NGÀY LẬP HÓA ĐƠN
    // =====================================================
    public int getPTGBTaiHD(HoaDon hd) {
        if (hd == null) return 0;

        LocalDate ngayHD = hd.getTgLapHD().toLocalDate();

        // KÍCH THƯỚC CACHE KEY theo ngày
        String keyMon = maMon + "|" + ngayHD;
        String keyLoai = loaiMon.getMaLoaiMon() + "|" + ngayHD;

        // Check cache món theo ngày
        if (CACHE_PT_MON_THEO_NGAY.containsKey(keyMon))
            return CACHE_PT_MON_THEO_NGAY.get(keyMon);

        // Check cache loại món theo ngày
        if (CACHE_PT_LOAIMON_THEO_NGAY.containsKey(keyLoai))
            return CACHE_PT_LOAIMON_THEO_NGAY.get(keyLoai);

        // Không có → Query DB 1 lần duy nhất (tránh query lặp)
        var ptMon = PhanTramGiaBanDAO.getEffectiveForMonAtDate(maMon, ngayHD);
        if (ptMon != null) {
            CACHE_PT_MON_THEO_NGAY.put(keyMon, ptMon.getPhanTramLoi());
            return ptMon.getPhanTramLoi();
        }

        var ptLoai = PhanTramGiaBanDAO.getEffectiveForLoaiMonAtDate(loaiMon.getMaLoaiMon(), ngayHD);
        if (ptLoai != null) {
            CACHE_PT_LOAIMON_THEO_NGAY.put(keyLoai, ptLoai.getPhanTramLoi());
            return ptLoai.getPhanTramLoi();
        }

        return 0;
    }

    public double getGiaBanTaiLucLapHD(HoaDon hd) {
        int pt = getPTGBTaiHD(hd);
        return giaGoc * (1 + pt / 100.0);
    }


    // ================= Getter/Setter ============
    public String getMaMon() { return maMon; }

    public void setMaMon(String maMon) {
        if(maMon == null || !maMon.matches("^MM\\d{4}$")) {
            throw new IllegalArgumentException("Mã món sai định dạng.");
        }
        this.maMon = maMon;
    }

    public String getTenMon() { return tenMon; }

    public void setTenMon(String tenMon) {
        if (tenMon == null || tenMon.trim().isEmpty()) {
            throw new IllegalArgumentException("Tên món không được để trống.");
        }
        this.tenMon = tenMon;
    }

    public String getMoTa() { return moTa; }
    public void setMoTa(String moTa) { this.moTa = moTa; }

    public String getHinhAnh() { return hinhAnh; }
    public void setHinhAnh(String hinhAnh) { this.hinhAnh = hinhAnh; }

    public LoaiMon getLoaiMon() { return loaiMon; }

    public void setLoaiMon(LoaiMon loaiMon) {
        if (loaiMon == null) {
            throw new IllegalArgumentException("Loại món không được để trống.");
        }
        this.loaiMon = loaiMon;
    }

    public double getGiaGoc() { return giaGoc; }

    public void setGiaGoc(double giaGoc) {
        if (giaGoc < 0) throw new IllegalArgumentException("Giá gốc không được nhỏ hơn 0.");
        this.giaGoc = giaGoc;
    }

    public int getSoLuong() { return soLuong; }
    public void setSoLuong(int soLuong) { this.soLuong = soLuong; }

    @Override
    public String toString() {
        return "Mon{" +
                "maMon='" + maMon + '\'' +
                ", tenMon='" + tenMon + '\'' +
                ", moTa='" + moTa + '\'' +
                ", hinhAnh='" + hinhAnh + '\'' +
                ", giaGoc=" + giaGoc +
                ", soLuong=" + soLuong +
                ", loaiMon=" + loaiMon +
                '}';
    }
}
