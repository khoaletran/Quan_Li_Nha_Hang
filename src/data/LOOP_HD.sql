USE QL_NhaHangCrabKing_Nhom02;

BEGIN TRY
    BEGIN TRAN;
	    DELETE FROM ChiTietHoaDon;
    DELETE FROM HoaDon;

    DECLARE @i INT = 1;
    DECLARE @SoHoaDon INT = 3000;

    DECLARE @Today DATE = CAST(GETDATE() AS DATE);

    WHILE @i <= @SoHoaDon
    BEGIN
        DECLARE @maHD NVARCHAR(13);
        DECLARE @maNV NVARCHAR(6);
        DECLARE @maKH NVARCHAR(6);
        DECLARE @maBan NVARCHAR(6);
        DECLARE @maKM NVARCHAR(6);
        DECLARE @maSK NVARCHAR(6);

        DECLARE @kieuThanhToan BIT = CAST(ROUND(RAND(CHECKSUM(NEWID())), 0) AS BIT);
        DECLARE @kieuDatBan BIT    = CAST(ROUND(RAND(CHECKSUM(NEWID())), 0) AS BIT);
        DECLARE @trangThai INT = 2; -- checkout
        DECLARE @soLuong INT;

        -- ====== NGÀY TỪ HÔM NAY TRỞ VỀ TRƯỚC ======
        -- Lùi 0..(6 năm ~ 2190 ngày) để chắc chắn <= hôm nay
        DECLARE @BackDays INT = ABS(CHECKSUM(NEWID())) % 2191; -- 0..2190
        DECLARE @Ngay DATE = DATEADD(DAY, -@BackDays, @Today); -- <= today

        -- tgLapHD, tgCheckin, tgCheckout: đảm bảo checkout >= checkin
        DECLARE @tgLapHD    SMALLDATETIME = DATEADD(MINUTE, ABS(CHECKSUM(NEWID())) % 600, CAST(@Ngay AS DATETIME)); -- +0..599'
        DECLARE @tgCheckin  SMALLDATETIME = DATEADD(MINUTE, 5 + (ABS(CHECKSUM(NEWID())) % 180), @tgLapHD);          -- +5..184'
        DECLARE @tgCheckout SMALLDATETIME = DATEADD(MINUTE, 30 + (ABS(CHECKSUM(NEWID())) % 300), @tgCheckin);       -- +30..329'

        -- Lấy ngẫu nhiên FK
        SET @maNV  = (SELECT TOP 1 maNV  FROM NhanVien ORDER BY NEWID());
        SET @maKH  = (SELECT TOP 1 maKH  FROM KhachHang ORDER BY NEWID());
        SET @maBan = (SELECT TOP 1 maBan FROM Ban ORDER BY NEWID());

        -- maKM, maSK cho phép NULL
        SET @maKM = CASE WHEN ABS(CHECKSUM(NEWID())) % 2 = 0
                         THEN (SELECT TOP 1 maKM FROM KhuyenMai ORDER BY NEWID())
                         ELSE NULL END;

        SET @maSK = CASE WHEN ABS(CHECKSUM(NEWID())) % 2 = 0
                         THEN (SELECT TOP 1 maSK FROM SuKien ORDER BY NEWID())
                         ELSE NULL END;

        -- Nếu thiếu dữ liệu nền thì bỏ qua vòng này
        IF @maNV IS NULL OR @maKH IS NULL OR @maBan IS NULL
        BEGIN
            SET @i = @i + 1;
            CONTINUE;
        END

        -- Tạo maHD đúng CHECK:
        -- LEN=13, 'HD', ký tự 3 in (0/1), 4 số cuối là số
        -- Format: HD + {0|1} + YYMMDD + ####
        DECLARE @flag CHAR(1) = CASE WHEN ABS(CHECKSUM(NEWID())) % 2 = 0 THEN '0' ELSE '1' END;
        DECLARE @YY CHAR(2) = RIGHT(CONVERT(VARCHAR(4), YEAR(@Ngay)), 2);
        DECLARE @MM CHAR(2) = RIGHT('0' + CONVERT(VARCHAR(2), MONTH(@Ngay)), 2);
        DECLARE @DD CHAR(2) = RIGHT('0' + CONVERT(VARCHAR(2), DAY(@Ngay)), 2);
        DECLARE @seq4 CHAR(4) = RIGHT('0000' + CONVERT(VARCHAR(10), @i), 4);

        SET @maHD = N'HD' + @flag + @YY + @MM + @DD + @seq4;

        -- số lượng tổng (>=0)
        SET @soLuong = 1 + (ABS(CHECKSUM(NEWID())) % 6); -- 1..6

        INSERT INTO HoaDon
        (
            maHD, maKH, maNV, maBan, maKM, maSK,
            tgLapHD, tgCheckin, tgCheckout,
            kieuThanhToan, kieuDatBan,
            trangThai, soLuong, moTa
        )
        VALUES
        (
            @maHD, @maKH, @maNV, @maBan, @maKM, @maSK,
            @tgLapHD, @tgCheckin, @tgCheckout,
            @kieuThanhToan, @kieuDatBan,
            @trangThai, @soLuong, N'Dữ liệu test'
        );

        -- =========================
        -- CHI TIẾT HÓA ĐƠN
        -- =========================
        DECLARE @j INT = 1;
        DECLARE @soDong INT = 1 + (ABS(CHECKSUM(NEWID())) % 4); -- 1..4

        WHILE @j <= @soDong
        BEGIN
            DECLARE @maMon NVARCHAR(6);
            DECLARE @sl INT;

            -- tránh trùng PK (maHD, maMon)
            SET @maMon = (
                SELECT TOP 1 m.maMon
                FROM Mon m
                WHERE NOT EXISTS (
                    SELECT 1
                    FROM ChiTietHoaDon ct
                    WHERE ct.maHD = @maHD AND ct.maMon = m.maMon
                )
                ORDER BY NEWID()
            );

            IF @maMon IS NULL BREAK;

            SET @sl = 1 + (ABS(CHECKSUM(NEWID())) % 5); -- >=1

            INSERT INTO ChiTietHoaDon(maHD, maMon, soLuong)
            VALUES (@maHD, @maMon, @sl);

            SET @j = @j + 1;
        END

        SET @i = @i + 1;
    END

    COMMIT TRAN;
END TRY
BEGIN CATCH
    IF @@TRANCOUNT > 0 ROLLBACK TRAN;

    -- Trả lỗi rõ ràng
    DECLARE @ErrMsg NVARCHAR(4000) = ERROR_MESSAGE();
    DECLARE @ErrLine INT = ERROR_LINE();
    DECLARE @ErrProc NVARCHAR(200) = ISNULL(ERROR_PROCEDURE(), N'(adhoc)');
    RAISERROR(N'Lỗi tại %s - line %d: %s', 16, 1, @ErrProc, @ErrLine, @ErrMsg);
END CATCH;
GO
--============================--
BEGIN TRY
    BEGIN TRAN;

    DECLARE @Now SMALLDATETIME = CAST(GETDATE() AS SMALLDATETIME);
    DECLARE @Today DATE = CAST(GETDATE() AS DATE);

    -- Lấy ngẫu nhiên FK nền
    DECLARE @maNV  NVARCHAR(6) = (SELECT TOP 1 maNV  FROM NhanVien ORDER BY NEWID());
    DECLARE @maKH  NVARCHAR(6) = (SELECT TOP 1 maKH  FROM KhachHang ORDER BY NEWID());
    DECLARE @maBan NVARCHAR(6) = (SELECT TOP 1 maBan FROM Ban ORDER BY NEWID());

    IF @maNV IS NULL OR @maKH IS NULL OR @maBan IS NULL
        THROW 50001, N'Thiếu dữ liệu nền (NhanVien/KhachHang/Ban) nên không thể insert HoaDon.', 1;

    -- Tạo 2 mã hóa đơn theo format: HD + {0|1} + YYMMDD + #### (LEN=13)
    -- #### lấy từ MAX hiện có trong ngày để tránh trùng
    DECLARE @YY CHAR(2) = RIGHT(CONVERT(VARCHAR(4), YEAR(@Today)), 2);
    DECLARE @MM CHAR(2) = RIGHT('0' + CONVERT(VARCHAR(2), MONTH(@Today)), 2);
    DECLARE @DD CHAR(2) = RIGHT('0' + CONVERT(VARCHAR(2), DAY(@Today)), 2);

    DECLARE @basePrefix NVARCHAR(9) = N'HD' + N'0' + @YY + @MM + @DD;  -- flag để '0' (có thể đổi)
    DECLARE @maxSeq INT =
    (
        SELECT ISNULL(MAX(TRY_CONVERT(INT, RIGHT(maHD, 4))), 0)
        FROM HoaDon
        WHERE maHD LIKE @basePrefix + N'%'
    );

    DECLARE @maHD1 NVARCHAR(13) = @basePrefix + RIGHT('0000' + CONVERT(VARCHAR(10), @maxSeq + 1), 4);
    DECLARE @maHD2 NVARCHAR(13) = @basePrefix + RIGHT('0000' + CONVERT(VARCHAR(10), @maxSeq + 2), 4);

    -- ========= INSERT 2 HÓA ĐƠN (trạng thái = 0) =========
    INSERT INTO HoaDon
    (
        maHD, maKH, maNV, maBan, maKM, maSK,
        tgLapHD, tgCheckin, tgCheckout,
        kieuThanhToan, kieuDatBan,
        trangThai, soLuong, moTa
    )
    VALUES
    (
        @maHD1, @maKH, @maNV, @maBan, NULL, NULL,
        @Now, @Now, NULL,                -- tgCheckout NULL vì trạng thái 0 (chưa checkout)
        0, 1,
        0, 2, N'Hóa đơn test (now) #1'
    ),
    (
        @maHD2, @maKH, @maNV, @maBan, NULL, NULL,
        @Now, @Now, NULL,
        0, 1,
        0, 2, N'Hóa đơn test (now) #2'
    );

    -- ========= CHI TIẾT HÓA ĐƠN: lấy vài món ngẫu nhiên =========
    ;WITH m AS (
        SELECT TOP 3 maMon
        FROM Mon
        ORDER BY NEWID()
    )
    INSERT INTO ChiTietHoaDon(maHD, maMon, soLuong)
    SELECT @maHD1, maMon, 1
    FROM m;

    ;WITH m AS (
        SELECT TOP 2 maMon
        FROM Mon
        ORDER BY NEWID()
    )
    INSERT INTO ChiTietHoaDon(maHD, maMon, soLuong)
    SELECT @maHD2, maMon, 2
    FROM m;

    COMMIT TRAN;
END TRY
BEGIN CATCH
    IF @@TRANCOUNT > 0 ROLLBACK TRAN;
    DECLARE @ErrMsg NVARCHAR(4000) = ERROR_MESSAGE();
    DECLARE @ErrLine INT = ERROR_LINE();
    DECLARE @ErrProc NVARCHAR(200) = ISNULL(ERROR_PROCEDURE(), N'(adhoc)');
    RAISERROR(N'Lỗi tại %s - line %d: %s', 16, 1, @ErrProc, @ErrLine, @ErrMsg);
END CATCH;
GO
delete from Coc


INSERT INTO Coc (maCoc, loaiCoc, phanTramCoc, soTienCoc, maLoaiBan, maKhuVuc)
VALUES
-- INDOOR
('CO0001', 0, 0, 200000, 'LB0001', 'KV0002'), -- IN A
('CO0002', 0, 0, 300000, 'LB0002', 'KV0002'), -- IN B
('CO0003', 1, 20, 0,      'LB0003', 'KV0002'), -- IN C
('CO0004', 1, 30, 0,      'LB0004', 'KV0002'), -- IN D

-- OUTDOOR
('CO0005', 0, 0, 100000, 'LB0001', 'KV0001'), -- OUT A
('CO0006', 0, 0, 200000, 'LB0002', 'KV0001'), -- OUT B
('CO0007', 1, 30, 0,      'LB0003', 'KV0001'), -- OUT C
('CO0008', 1, 35, 0,      'LB0004', 'KV0001'), -- OUT D

-- VIP
('CO0009', 1, 38, 0,      'LB0004', 'KV0003'), -- VIP D
('CO0010', 1, 38, 0,      'LB0005', 'KV0003'); -- VIP E

select * from Coc

