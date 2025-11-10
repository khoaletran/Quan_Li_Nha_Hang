-- =========================================
-- TẠO CƠ SỞ DỮ LIỆU QUÁN CAFE / NHÀ HÀNG
-- =========================================

CREATE DATABASE QL_NhaHangCrabKing_Nhom02;
GO

USE QL_NhaHangCrabKing_Nhom02;
GO

-- =========================================
-- TẠO LOGIN VÀ USER
-- =========================================

-- Tạo login ở cấp server
CREATE LOGIN nhanvien_app
    WITH PASSWORD = '123456',
    CHECK_POLICY = OFF; -- bỏ kiểm tra độ mạnh mật khẩu (cho dễ test)
GO

-- Tạo user trong database
CREATE USER nhanvien_app FOR LOGIN nhanvien_app;
GO

-- Cấp quyền thao tác cơ bản
EXEC sp_addrolemember 'db_datareader', 'nhanvien_app';
EXEC sp_addrolemember 'db_datawriter', 'nhanvien_app';
GO

-- =========================================
-- BẢNG HẠNG KHÁCH HÀNG
-- =========================================
CREATE TABLE HangKhachHang
(
    maHang   NVARCHAR(6) PRIMARY KEY,
    diemHang INT           NOT NULL,
    giamGia  INT           NOT NULL,
    moTa     NVARCHAR(200) NULL,
    CONSTRAINT chk_diemHang_HKH CHECK (diemHang >= 0),
    CONSTRAINT chk_giamGia_HKH CHECK (giamGia BETWEEN 0 AND 100),
    CONSTRAINT chk_maHang_HKH CHECK (maHang LIKE 'HH[0-9][0-9][0-9][0-9]')
);

-- =========================================
-- BẢNG KHÁCH HÀNG
-- =========================================
CREATE TABLE KhachHang
(
    maKH        NVARCHAR(6) PRIMARY KEY,
    maHang      NVARCHAR(6) FOREIGN KEY REFERENCES HangKhachHang (maHang),
    tenKH       NVARCHAR(50),
    sdt         NVARCHAR(10),
    gioiTinh    BIT,
    diemTichLuy INT,
    CONSTRAINT chk_maKH_KH CHECK (maKH LIKE 'KH[0-9][0-9][0-9][0-9]'),
    CONSTRAINT chk_sdt_KH CHECK (sdt LIKE '0[3-9][0-9][0-9][0-9][0-9][0-9][0-9][0-9][0-9]'),
    CONSTRAINT chk_gioiTinh_KH CHECK (gioiTinh IN (0, 1)),
    CONSTRAINT chk_diemTichLuy_KH CHECK (diemTichLuy >= 0)
);

-- =========================================
-- BẢNG NHÂN VIÊN
-- =========================================
CREATE TABLE NhanVien
(
    maNV       NVARCHAR(6) PRIMARY KEY,
    tenNV      NVARCHAR(50) NOT NULL,
    sdt        NVARCHAR(10),
    gioiTinh   BIT,
    quanLi     BIT,
    ngayVaoLam DATE,
    trangThai  BIT,
    matKhau    NVARCHAR(50),
    CONSTRAINT chk_maNV_NV CHECK (maNV LIKE 'NV[0-9][0-9][0-9][0-9]'),
    CONSTRAINT chk_sdt_NV CHECK (sdt LIKE '0[3-9][0-9][0-9][0-9][0-9][0-9][0-9][0-9][0-9]'),
    CONSTRAINT chk_gioiTinh_NV CHECK (gioiTinh IN (0, 1)),
    CONSTRAINT chk_quanLi_NV CHECK (quanLi IN (0, 1)),
    CONSTRAINT chk_ngayVaoLam_NV CHECK (ngayVaoLam <= GETDATE()),
    CONSTRAINT chk_trangThai_NV CHECK (trangThai IN (0, 1))
);

-- =========================================
-- BẢNG LOẠI BÀN
-- =========================================
CREATE TABLE LoaiBan
(
    maLoaiBan  NVARCHAR(6) PRIMARY KEY,
    tenLoaiBan NVARCHAR(50) NOT NULL,
    soLuong    INT          NOT NULL,
    CONSTRAINT chk_maLoaiBan_LB CHECK (maLoaiBan LIKE 'LB[0-9][0-9][0-9][0-9]'),
    CONSTRAINT chk_soLuong_LB CHECK (soLuong >= 0)
);

-- =========================================
-- BẢNG KHU VỰC
-- =========================================
CREATE TABLE KhuVuc
(
    maKhuVuc  NVARCHAR(6) PRIMARY KEY,
    tenKhuVuc NVARCHAR(50),
    CONSTRAINT chk_maKhuVuc_KV CHECK (maKhuVuc LIKE 'KV[0-9][0-9][0-9][0-9]')
);

-- =========================================
-- BẢNG BÀN
-- =========================================
CREATE TABLE Ban
(
    maBan     NVARCHAR(6) PRIMARY KEY,
    trangThai BIT,
    maLoaiBan NVARCHAR(6) FOREIGN KEY REFERENCES LoaiBan (maLoaiBan),
    maKhuVuc  NVARCHAR(6) FOREIGN KEY REFERENCES KhuVuc (maKhuVuc),
    CONSTRAINT chk_maBan_BAN CHECK (maBan LIKE '[BW][OIV][0-9][0-9][0-9][0-9]'),
    CONSTRAINT chk_trangThai_BAN CHECK (trangThai IN (0, 1))
);

ALTER TABLE Ban
    ADD CONSTRAINT chk_maBan_BAN CHECK (
        maBan LIKE '[BW][OIV][0-9][0-9][0-9][0-9]'
        );
-- =========================================
-- BẢNG SỰ KIỆN
-- =========================================
CREATE TABLE SuKien
(
    maSK  NVARCHAR(6) PRIMARY KEY,
    tenSK NVARCHAR(50),
    moTa  NVARCHAR(200),
    gia   FLOAT,
    CONSTRAINT chk_maSK_SK CHECK (maSK LIKE 'SK[0-9][0-9][0-9][0-9]'),
    CONSTRAINT chk_gia_SK CHECK (gia >= 0)
);

-- =========================================
-- BẢNG THỜI GIAN ĐỔI BÀN
-- =========================================
CREATE TABLE ThoiGianDoiBan
(
    maTGDB     NVARCHAR(6) PRIMARY KEY,
    loaiDatBan BIT,
    thoiGian   INT,
    CONSTRAINT chk_maTGDB_TG CHECK (maTGDB LIKE 'TD[0-9][0-9][0-9][0-9]'),
    CONSTRAINT chk_loaiDatBan_TG CHECK (loaiDatBan IN (0, 1)),
    CONSTRAINT chk_thoiGian_TG CHECK (thoiGian > 0)
);

-- =========================================
-- BẢNG CỌC
-- =========================================
CREATE TABLE Coc
(
    maCoc       NVARCHAR(6) PRIMARY KEY,
    loaiCoc     BIT,
    phanTramCoc INT,
    soTienCoc   FLOAT,
    maLoaiBan   NVARCHAR(6) FOREIGN KEY REFERENCES LoaiBan (maLoaiBan),
    maKhuVuc    NVARCHAR(6) FOREIGN KEY REFERENCES KhuVuc (maKhuVuc),
    CONSTRAINT chk_maCoc_COC CHECK (maCoc LIKE 'CO[0-9][0-9][0-9][0-9]'),
    CONSTRAINT chk_loaiCoc_COC CHECK (loaiCoc IN (0, 1)),
    CONSTRAINT chk_phanTramCoc_COC CHECK ((loaiCoc = 1 AND phanTramCoc BETWEEN 0 AND 100) OR loaiCoc = 0),
    CONSTRAINT chk_soTienCoc_COC CHECK ((loaiCoc = 0 AND soTienCoc >= 0) OR loaiCoc = 1)
);

-- =========================================
-- BẢNG LOẠI MÓN
-- =========================================
CREATE TABLE LoaiMon
(
    maLoaiMon  NVARCHAR(6) PRIMARY KEY,
    tenLoaiMon NVARCHAR(50),
    moTa       NVARCHAR(200),
    CONSTRAINT chk_maLoaiMon_LM CHECK (maLoaiMon LIKE 'LM[0-9][0-9][0-9][0-9]')
);

-- =========================================
-- BẢNG KHUYẾN MÃI
-- =========================================
CREATE TABLE KhuyenMai
(
    maKM            NVARCHAR(6) PRIMARY KEY,
    tenKM           NVARCHAR(50),
    soLuong         INT,
    ngayPhatHanh    DATE,
    ngayKetThuc     DATE,
    maThayThe       NVARCHAR(10),
    phanTramGiamGia INT,
    uuDai           BIT,
    CONSTRAINT chk_maKM_KM CHECK (maKM LIKE 'KM[0-9][0-9][0-9][0-9]'),
    CONSTRAINT chk_soLuong_KM CHECK (soLuong >= 0),
    CONSTRAINT chk_phanTramGiamGia_KM CHECK (phanTramGiamGia BETWEEN 0 AND 100),
    CONSTRAINT chk_ngayPhatHanh_KM CHECK (ngayPhatHanh >= CAST(GETDATE() AS DATE)),
    CONSTRAINT chk_ngayKetThuc_KM CHECK (ngayKetThuc > ngayPhatHanh),
    CONSTRAINT chk_uuDai_KM CHECK (uuDai IN (0, 1))
);

-- =========================================
-- BẢNG MÓN
-- =========================================
CREATE TABLE Mon
(
    maMon   NVARCHAR(6) PRIMARY KEY,
    tenMon  NVARCHAR(50),
    moTa    NVARCHAR(50),
    hinhAnh NVARCHAR(50),
    giaGoc  FLOAT,
    soLuong INT,
    loaiMon NVARCHAR(6) FOREIGN KEY REFERENCES LoaiMon (maLoaiMon),
    CONSTRAINT chk_maMon_MON CHECK (maMon LIKE 'MM[0-9][0-9][0-9][0-9]'),
    CONSTRAINT chk_giaGoc_MON CHECK (giaGoc > 0),
    CONSTRAINT chk_soLuong_MON CHECK (soLuong >= 0)
);
-- =========================================
-- BẢNG PHẦN TRĂM GIÁ BÁN
-- =========================================
CREATE TABLE PhanTramGiaBan
(
    maPTGB      NVARCHAR(6) PRIMARY KEY,
    maLoaiMon   NVARCHAR(6) FOREIGN KEY REFERENCES LoaiMon (maLoaiMon),
    maMon       NVARCHAR(6) FOREIGN KEY REFERENCES Mon (maMon),
    phanTramLoi INT,
    ngayApDung  DATE,
    CONSTRAINT chk_maPTGB_PG CHECK (maPTGB LIKE 'PG[0-9][0-9][0-9][0-9]'),
    CONSTRAINT chk_phanTramLoi_PG CHECK (phanTramLoi >= 0)
);

-- =========================================
-- BẢNG HÓA ĐƠN
-- =========================================
CREATE TABLE HoaDon
(
    maHD          NVARCHAR(13) PRIMARY KEY,
    maKH          NVARCHAR(6) FOREIGN KEY REFERENCES KhachHang (maKH),
    maNV          NVARCHAR(6) FOREIGN KEY REFERENCES NhanVien (maNV),
    maBan         NVARCHAR(6) FOREIGN KEY REFERENCES Ban (maBan),
    maKM          NVARCHAR(6) FOREIGN KEY REFERENCES KhuyenMai (maKM),
    maSK          NVARCHAR(6) FOREIGN KEY REFERENCES SuKien (maSK),
    tgLapHD       SMALLDATETIME,
    tgCheckin     SMALLDATETIME,
    tgCheckout    SMALLDATETIME,
    kieuThanhToan BIT, -- 1: ck, 0: tiền mặt
    kieuDatBan    BIT,
    trangThai     INT, -- 0 đã đặt, 1 checkin, 2 checkout, 3 hủy bàn
    soLuong       INT,
    moTa          NVARCHAR(200),
    CONSTRAINT chk_maHD_HD CHECK (
        LEN(maHD) = 13 AND
        LEFT(maHD, 2) = 'HD' AND
        SUBSTRING(maHD, 3, 1) IN ('0', '1') AND
        ISNUMERIC(RIGHT(maHD, 4)) = 1
        ),
    CONSTRAINT chk_trangThai_HD CHECK (trangThai BETWEEN 0 AND 3),
    CONSTRAINT chk_tgCheckout_HD CHECK (tgCheckout IS NULL OR tgCheckout >= tgCheckin),
    CONSTRAINT chk_kieuThanhToan_HD CHECK (kieuThanhToan IN (0, 1)),
    CONSTRAINT chk_kieuDatBan_HD CHECK (kieuDatBan IN (0, 1)),
    CONSTRAINT chk_soLuong_HD CHECK (soLuong >= 0)
);

-- =========================================
-- BẢNG CHI TIẾT HÓA ĐƠN
-- =========================================
CREATE TABLE ChiTietHoaDon
(
    maHD    NVARCHAR(13) FOREIGN KEY REFERENCES HoaDon (maHD),
    maMon   NVARCHAR(6) FOREIGN KEY REFERENCES Mon (maMon),
    soLuong INT,
    PRIMARY KEY (maHD, maMon),
    CONSTRAINT chk_soLuong_CT CHECK (soLuong >= 1)
);

-- =========================================
-- BẢNG PHIẾU KẾT CA
-- =========================================
CREATE TABLE PhieuKetCa
(
    maPhieu       NVARCHAR(6) PRIMARY KEY,
    maNV          NVARCHAR(6) FOREIGN KEY REFERENCES NhanVien (maNV),
    ca            BIT, -- 0: ca sáng, 1: ca tối
    soHoaDon      INT,
    tienMat       FLOAT,
    tienCK        FLOAT,
    tienChenhLech FLOAT,
    ngayKetCa     SMALLDATETIME,
    moTa          NVARCHAR(MAX),

);
GO
ALTER TABLE KhuyenMai
    DROP CONSTRAINT chk_ngayPhatHanh_KM;
GO