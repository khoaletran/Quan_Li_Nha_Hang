-- =========================================
-- TẠO CƠ SỞ DỮ LIỆU QUÁN CAFE / NHÀ HÀNG
-- (BẢN HOÀN CHỈNH - KHÔNG DÙNG ALTER)
-- =========================================

IF DB_ID('QL_NhaHangCrabKing_Nhom02') IS NOT NULL
    BEGIN
        ALTER DATABASE QL_NhaHangCrabKing_Nhom02 SET SINGLE_USER WITH ROLLBACK IMMEDIATE;
        DROP DATABASE QL_NhaHangCrabKing_Nhom02;
    END
GO

CREATE DATABASE QL_NhaHangCrabKing_Nhom02;
GO

USE QL_NhaHangCrabKing_Nhom02;
GO

-- =========================================
-- TẠO LOGIN VÀ USER
-- =========================================
IF NOT EXISTS (SELECT 1 FROM sys.server_principals WHERE name = N'nhanvien_app')
    BEGIN
        CREATE LOGIN nhanvien_app
            WITH PASSWORD = '123456',
            CHECK_POLICY = OFF;
    END
GO

IF NOT EXISTS (SELECT 1 FROM sys.database_principals WHERE name = N'nhanvien_app')
    BEGIN
        CREATE USER nhanvien_app FOR LOGIN nhanvien_app;
    END
GO

EXEC sp_addrolemember 'db_datareader', 'nhanvien_app';
EXEC sp_addrolemember 'db_datawriter', 'nhanvien_app';
GO

-- =========================================
-- BẢNG HẠNG KHÁCH HÀNG
-- =========================================
CREATE TABLE HangKhachHang
(
    maHang   NVARCHAR(6)  NOT NULL,
    diemHang INT          NOT NULL,
    giamGia  INT          NOT NULL,
    moTa     NVARCHAR(200) NULL,

    CONSTRAINT PK_HangKhachHang PRIMARY KEY (maHang),
    CONSTRAINT chk_maHang_HKH   CHECK (maHang LIKE 'HH[0-9][0-9][0-9][0-9]'),
    CONSTRAINT chk_diemHang_HKH CHECK (diemHang >= 0),
    CONSTRAINT chk_giamGia_HKH  CHECK (giamGia BETWEEN 0 AND 100)
);
GO

-- =========================================
-- BẢNG KHÁCH HÀNG
-- =========================================
CREATE TABLE KhachHang
(
    maKH        NVARCHAR(6)  NOT NULL,
    maHang      NVARCHAR(6)  NULL,
    tenKH       NVARCHAR(50) NULL,
    sdt         NVARCHAR(10) NULL,
    gioiTinh    BIT          NULL,
    diemTichLuy INT          NULL,

    CONSTRAINT PK_KhachHang PRIMARY KEY (maKH),
    CONSTRAINT FK_KhachHang_HangKhachHang FOREIGN KEY (maHang) REFERENCES HangKhachHang (maHang),

    CONSTRAINT chk_maKH_KH CHECK (maKH LIKE 'KH[0-9][0-9][0-9][0-9]'),
    CONSTRAINT chk_sdt_KH  CHECK (sdt IS NULL OR sdt LIKE '0[3-9][0-9][0-9][0-9][0-9][0-9][0-9][0-9][0-9]'),
    CONSTRAINT chk_gioiTinh_KH CHECK (gioiTinh IS NULL OR gioiTinh IN (0, 1)),
    CONSTRAINT chk_diemTichLuy_KH CHECK (diemTichLuy IS NULL OR diemTichLuy >= 0)
);
GO

-- =========================================
-- BẢNG NHÂN VIÊN
-- =========================================
CREATE TABLE NhanVien
(
    maNV       NVARCHAR(6)  NOT NULL,
    tenNV      NVARCHAR(50) NOT NULL,
    sdt        NVARCHAR(10) NULL,
    gioiTinh   BIT          NULL,
    quanLi     BIT          NULL,
    ngayVaoLam DATE         NULL,
    trangThai  BIT          NULL,
    matKhau    NVARCHAR(50) NULL,

    CONSTRAINT PK_NhanVien PRIMARY KEY (maNV),
    CONSTRAINT chk_maNV_NV CHECK (maNV LIKE 'NV[0-9][0-9][0-9][0-9]'),
    CONSTRAINT chk_sdt_NV  CHECK (sdt IS NULL OR sdt LIKE '0[3-9][0-9][0-9][0-9][0-9][0-9][0-9][0-9][0-9]'),
    CONSTRAINT chk_gioiTinh_NV CHECK (gioiTinh IS NULL OR gioiTinh IN (0, 1)),
    CONSTRAINT chk_quanLi_NV   CHECK (quanLi IS NULL OR quanLi IN (0, 1)),
    CONSTRAINT chk_ngayVaoLam_NV CHECK (ngayVaoLam IS NULL OR ngayVaoLam <= CAST(GETDATE() AS DATE)),
    CONSTRAINT chk_trangThai_NV  CHECK (trangThai IS NULL OR trangThai IN (0, 1))
);
GO

-- =========================================
-- BẢNG LOẠI BÀN
-- =========================================
CREATE TABLE LoaiBan
(
    maLoaiBan  NVARCHAR(6)  NOT NULL,
    tenLoaiBan NVARCHAR(50) NOT NULL,
    soLuong    INT          NOT NULL,

    CONSTRAINT PK_LoaiBan PRIMARY KEY (maLoaiBan),
    CONSTRAINT chk_maLoaiBan_LB CHECK (maLoaiBan LIKE 'LB[0-9][0-9][0-9][0-9]'),
    CONSTRAINT chk_soLuong_LB CHECK (soLuong >= 0)
);
GO

-- =========================================
-- BẢNG KHU VỰC
-- =========================================
CREATE TABLE KhuVuc
(
    maKhuVuc  NVARCHAR(6)  NOT NULL,
    tenKhuVuc NVARCHAR(50) NULL,

    CONSTRAINT PK_KhuVuc PRIMARY KEY (maKhuVuc),
    CONSTRAINT chk_maKhuVuc_KV CHECK (maKhuVuc LIKE 'KV[0-9][0-9][0-9][0-9]')
);
GO

-- =========================================
-- BẢNG BÀN
-- =========================================
CREATE TABLE Ban
(
    maBan     NVARCHAR(6) NOT NULL,
    trangThai BIT         NULL,
    maLoaiBan NVARCHAR(6) NULL,
    maKhuVuc  NVARCHAR(6) NULL,

    CONSTRAINT PK_Ban PRIMARY KEY (maBan),
    CONSTRAINT FK_Ban_LoaiBan FOREIGN KEY (maLoaiBan) REFERENCES LoaiBan (maLoaiBan),
    CONSTRAINT FK_Ban_KhuVuc  FOREIGN KEY (maKhuVuc)  REFERENCES KhuVuc (maKhuVuc),

    CONSTRAINT chk_maBan_BAN CHECK (maBan LIKE '[BW][OIV][0-9][0-9][0-9][0-9]'),
    CONSTRAINT chk_trangThai_BAN CHECK (trangThai IS NULL OR trangThai IN (0, 1))
);
GO

-- =========================================
-- BẢNG SỰ KIỆN
-- =========================================
CREATE TABLE SuKien
(
    maSK  NVARCHAR(6)  NOT NULL,
    tenSK NVARCHAR(50) NULL,
    moTa  NVARCHAR(200) NULL,
    gia   FLOAT        NULL,

    CONSTRAINT PK_SuKien PRIMARY KEY (maSK),
    CONSTRAINT chk_maSK_SK CHECK (maSK LIKE 'SK[0-9][0-9][0-9][0-9]'),
    CONSTRAINT chk_gia_SK CHECK (gia IS NULL OR gia >= 0)
);
GO

-- =========================================
-- BẢNG THỜI GIAN ĐỔI BÀN
-- =========================================
CREATE TABLE ThoiGianDoiBan
(
    maTGDB     NVARCHAR(6) NOT NULL,
    loaiDatBan BIT         NULL,
    thoiGian   INT         NULL,

    CONSTRAINT PK_ThoiGianDoiBan PRIMARY KEY (maTGDB),
    CONSTRAINT chk_maTGDB_TG CHECK (maTGDB LIKE 'TD[0-9][0-9][0-9][0-9]'),
    CONSTRAINT chk_loaiDatBan_TG CHECK (loaiDatBan IS NULL OR loaiDatBan IN (0, 1)),
    CONSTRAINT chk_thoiGian_TG CHECK (thoiGian IS NULL OR thoiGian > 0)
);
GO

-- =========================================
-- BẢNG CỌC
-- =========================================
CREATE TABLE Coc
(
    maCoc       NVARCHAR(6) NOT NULL,
    loaiCoc     BIT         NULL,
    phanTramCoc INT         NULL,
    soTienCoc   FLOAT       NULL,
    maLoaiBan   NVARCHAR(6) NULL,
    maKhuVuc    NVARCHAR(6) NULL,

    CONSTRAINT PK_Coc PRIMARY KEY (maCoc),
    CONSTRAINT FK_Coc_LoaiBan FOREIGN KEY (maLoaiBan) REFERENCES LoaiBan (maLoaiBan),
    CONSTRAINT FK_Coc_KhuVuc  FOREIGN KEY (maKhuVuc)  REFERENCES KhuVuc (maKhuVuc),

    CONSTRAINT chk_maCoc_COC CHECK (maCoc LIKE 'CO[0-9][0-9][0-9][0-9]'),
    CONSTRAINT chk_loaiCoc_COC CHECK (loaiCoc IS NULL OR loaiCoc IN (0, 1)),
    CONSTRAINT chk_phanTramCoc_COC CHECK (
        loaiCoc IS NULL
            OR (loaiCoc = 1 AND phanTramCoc BETWEEN 0 AND 100)
            OR (loaiCoc = 0)
        ),
    CONSTRAINT chk_soTienCoc_COC CHECK (
        loaiCoc IS NULL
            OR (loaiCoc = 0 AND soTienCoc >= 0)
            OR (loaiCoc = 1)
        )
);
GO

-- =========================================
-- BẢNG LOẠI MÓN
-- =========================================
CREATE TABLE LoaiMon
(
    maLoaiMon  NVARCHAR(6)  NOT NULL,
    tenLoaiMon NVARCHAR(50) NULL,
    moTa       NVARCHAR(200) NULL,

    CONSTRAINT PK_LoaiMon PRIMARY KEY (maLoaiMon),
    CONSTRAINT chk_maLoaiMon_LM CHECK (maLoaiMon LIKE 'LM[0-9][0-9][0-9][0-9]')
);
GO

-- =========================================
-- BẢNG KHUYẾN MÃI
-- =========================================
CREATE TABLE KhuyenMai
(
    maKM            NVARCHAR(6)  NOT NULL,
    tenKM           NVARCHAR(50) NULL,
    soLuong         INT          NULL,
    ngayPhatHanh    DATE         NULL,
    ngayKetThuc     DATE         NULL,
    maThayThe       NVARCHAR(10) NULL,
    phanTramGiamGia INT          NULL,
    uuDai           BIT          NULL,

    CONSTRAINT PK_KhuyenMai PRIMARY KEY (maKM),
    CONSTRAINT chk_maKM_KM CHECK (maKM LIKE 'KM[0-9][0-9][0-9][0-9]'),
    CONSTRAINT chk_soLuong_KM CHECK (soLuong IS NULL OR soLuong >= 0),
    -- Lưu ý: không ép ngayPhatHanh >= GETDATE() để còn nhập dữ liệu quá khứ/test
    CONSTRAINT chk_ngayKetThuc_KM CHECK (
        ngayPhatHanh IS NULL OR ngayKetThuc IS NULL OR ngayKetThuc > ngayPhatHanh
        ),
    CONSTRAINT chk_uuDai_KM CHECK (uuDai IS NULL OR uuDai IN (0, 1)),
    CONSTRAINT chk_phanTramGiamGia_KM CHECK (
        uuDai IS NULL
            OR (uuDai = 0 AND phanTramGiamGia BETWEEN 1 AND 100)
            OR (uuDai = 1 AND phanTramGiamGia >= 0)
        )
);
GO

-- =========================================
-- BẢNG MÓN
-- =========================================
CREATE TABLE Mon
(
    maMon   NVARCHAR(6)  NOT NULL,
    tenMon  NVARCHAR(50) NULL,
    moTa    NVARCHAR(50) NULL,
    hinhAnh NVARCHAR(50) NULL,
    giaGoc  FLOAT        NULL,
    soLuong INT          NULL,
    loaiMon NVARCHAR(6)  NULL,

    CONSTRAINT PK_Mon PRIMARY KEY (maMon),
    CONSTRAINT FK_Mon_LoaiMon FOREIGN KEY (loaiMon) REFERENCES LoaiMon (maLoaiMon),

    CONSTRAINT chk_maMon_MON CHECK (maMon LIKE 'MM[0-9][0-9][0-9][0-9]'),
    CONSTRAINT chk_giaGoc_MON CHECK (giaGoc IS NULL OR giaGoc > 0),
    CONSTRAINT chk_soLuong_MON CHECK (soLuong IS NULL OR soLuong >= 0)
);
GO

-- =========================================
-- BẢNG PHẦN TRĂM GIÁ BÁN
-- =========================================
CREATE TABLE PhanTramGiaBan
(
    maPTGB      NVARCHAR(6)  NOT NULL,
    maLoaiMon   NVARCHAR(6)  NULL,
    maMon       NVARCHAR(6)  NULL,
    phanTramLoi INT          NULL,
    ngayApDung  SMALLDATETIME NULL,

    CONSTRAINT PK_PhanTramGiaBan PRIMARY KEY (maPTGB),
    CONSTRAINT FK_PTGB_LoaiMon FOREIGN KEY (maLoaiMon) REFERENCES LoaiMon (maLoaiMon),
    CONSTRAINT FK_PTGB_Mon     FOREIGN KEY (maMon)     REFERENCES Mon (maMon),

    CONSTRAINT chk_maPTGB_PG CHECK (maPTGB LIKE 'PG[0-9][0-9][0-9][0-9]'),
    CONSTRAINT chk_phanTramLoi_PG CHECK (phanTramLoi IS NULL OR phanTramLoi >= 0)
);
GO

-- =========================================
-- BẢNG HÓA ĐƠN
-- =========================================
CREATE TABLE HoaDon
(
    maHD          NVARCHAR(13) NOT NULL,
    maKH          NVARCHAR(6)  NULL,
    maNV          NVARCHAR(6)  NULL,
    maBan         NVARCHAR(6)  NULL,
    maKM          NVARCHAR(6)  NULL,
    maSK          NVARCHAR(6)  NULL,
    tgLapHD       SMALLDATETIME NULL,
    tgCheckin     SMALLDATETIME NULL,
    tgCheckout    SMALLDATETIME NULL,
    kieuThanhToan BIT          NULL, -- 1: ck, 0: tiền mặt
    kieuDatBan    BIT          NULL,
    trangThai     INT          NULL, -- 0 đã đặt, 1 checkin, 2 checkout, 3 hủy bàn
    soLuong       INT          NULL,
    moTa          NVARCHAR(200) NULL,

    CONSTRAINT PK_HoaDon PRIMARY KEY (maHD),
    CONSTRAINT FK_HoaDon_KhachHang FOREIGN KEY (maKH) REFERENCES KhachHang (maKH),
    CONSTRAINT FK_HoaDon_NhanVien  FOREIGN KEY (maNV) REFERENCES NhanVien (maNV),
    CONSTRAINT FK_HoaDon_Ban       FOREIGN KEY (maBan) REFERENCES Ban (maBan),
    CONSTRAINT FK_HoaDon_KhuyenMai FOREIGN KEY (maKM) REFERENCES KhuyenMai (maKM),
    CONSTRAINT FK_HoaDon_SuKien    FOREIGN KEY (maSK) REFERENCES SuKien (maSK),

    CONSTRAINT chk_maHD_HD CHECK (
        LEN(maHD) = 13 AND
        LEFT(maHD, 2) = 'HD' AND
        SUBSTRING(maHD, 3, 1) IN ('0', '1') AND
        ISNUMERIC(RIGHT(maHD, 4)) = 1
        ),
    CONSTRAINT chk_trangThai_HD CHECK (trangThai IS NULL OR trangThai BETWEEN 0 AND 3),
    CONSTRAINT chk_tgCheckout_HD CHECK (tgCheckout IS NULL OR tgCheckin IS NULL OR tgCheckout >= tgCheckin),
    CONSTRAINT chk_kieuThanhToan_HD CHECK (kieuThanhToan IS NULL OR kieuThanhToan IN (0, 1)),
    CONSTRAINT chk_kieuDatBan_HD CHECK (kieuDatBan IS NULL OR kieuDatBan IN (0, 1)),
    CONSTRAINT chk_soLuong_HD CHECK (soLuong IS NULL OR soLuong >= 0)
);
GO

-- =========================================
-- BẢNG CHI TIẾT HÓA ĐƠN
-- =========================================
CREATE TABLE ChiTietHoaDon
(
    maHD    NVARCHAR(13) NOT NULL,
    maMon   NVARCHAR(6)  NOT NULL,
    soLuong INT          NULL,

    CONSTRAINT PK_ChiTietHoaDon PRIMARY KEY (maHD, maMon),
    CONSTRAINT FK_CTHD_HoaDon FOREIGN KEY (maHD) REFERENCES HoaDon (maHD),
    CONSTRAINT FK_CTHD_Mon    FOREIGN KEY (maMon) REFERENCES Mon (maMon),

    CONSTRAINT chk_soLuong_CT CHECK (soLuong IS NULL OR soLuong >= 1)
);
GO

-- =========================================
-- BẢNG PHIẾU KẾT CA
-- =========================================
CREATE TABLE PhieuKetCa
(
    maPhieu       NVARCHAR(13) NOT NULL,
    maNV          NVARCHAR(6)  NULL,
    ca            BIT          NULL, -- 0: ca sáng, 1: ca tối
    soHoaDon      INT          NULL,
    tienMat       FLOAT        NULL,
    tienCK        FLOAT        NULL,
    tienChenhLech FLOAT        NULL,
    ngayKetCa     SMALLDATETIME NULL,
    tgLogIn       SMALLDATETIME NULL,
    moTa          NVARCHAR(MAX) NULL,

    CONSTRAINT PK_PhieuKetCa PRIMARY KEY (maPhieu),
    CONSTRAINT FK_PhieuKetCa_NhanVien FOREIGN KEY (maNV) REFERENCES NhanVien (maNV),
    CONSTRAINT chk_ca_PKC CHECK (ca IS NULL OR ca IN (0, 1)),
    CONSTRAINT chk_soHoaDon_PKC CHECK (soHoaDon IS NULL OR soHoaDon >= 0)
);
GO

-- =========================================
-- TRIGGER: CẬP NHẬT HẠNG KHÁCH HÀNG THEO ĐIỂM
-- =========================================
CREATE TRIGGER trg_UpdateHangKhachHang
    ON KhachHang
    AFTER UPDATE
    AS
BEGIN
    SET NOCOUNT ON;

    IF UPDATE(diemTichLuy)
        BEGIN
            UPDATE KH
            SET maHang = HH.maHang
            FROM KhachHang KH
                     JOIN inserted i ON KH.maKH = i.maKH
                     CROSS APPLY (
                SELECT TOP 1 maHang
                FROM HangKhachHang
                WHERE diemHang <= i.diemTichLuy
                ORDER BY diemHang DESC
            ) HH;
        END
END;
GO
