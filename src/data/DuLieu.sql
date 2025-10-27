USE QL_NhaHangCrabKing_Nhom02;
GO

-- =============== HANG KHÁCH HÀNG ===============
INSERT INTO HangKhachHang VALUES
('HH0001', 0, 0, N'Hạng Đồng - Mới tham gia'),
('HH0002', 200, 5, N'Hạng Bạc - Khách thân thiết'),
('HH0003', 500, 10, N'Hạng Vàng - Khách VIP nhỏ'),
('HH0004', 1000, 15, N'Hạng Bạch Kim - Khách VIP lớn'),
('HH0005', 2000, 20, N'Hạng Kim Cương - Khách siêu VIP');

-- =============== KHÁCH HÀNG ===============
INSERT INTO KhachHang VALUES
('KH0001', 'HH0001', N'Lê Minh Tuấn', '0912345678', 1, 120),
('KH0002', 'HH0002', N'Nguyễn Thị Hoa', '0987654321', 0, 350),
('KH0003', 'HH0003', N'Trần Quốc Nhã', '0905123456', 1, 700),
('KH0004', 'HH0004', N'Đỗ Minh Quân', '0977333444', 1, 1100),
('KH0005', 'HH0005', N'Trần Lê Khoa', '0933444555', 1, 2100);

-- =============== NHÂN VIÊN ===============
INSERT INTO NhanVien VALUES
('NV0001', N'Nguyễn Hà Nhật Khanh', '0911002233', 0, 1, '2024-12-01', 1, '1'),
('NV0002', N'Trần Lê Khoa', '0909123123', 1, 1, '2025-01-05', 1, '1'),
('NV0003', N'Đỗ Minh Quân', '0977333555', 1, 0, '2025-02-20', 1, '1'),
('NV0004', N'Lê Hồng Nhung', '0967222333', 0, 0, '2025-03-12', 1, '1'),
('NV0005', N'Phạm Gia Huy', '0915666777', 1, 0, '2025-04-01', 0, '1');

-- =============== LOẠI BÀN ===============
INSERT INTO LoaiBan VALUES
('LB0001', N'A', 2),
('LB0002', N'B', 4),
('LB0003', N'C', 8),
('LB0004', N'D', 12),
('LB0005', N'E', 20);

-- =============== KHU VỰC ===============
INSERT INTO KhuVuc VALUES
('KV0001', N'Indoor'),
('KV0002', N'Outdoor'),
('KV0003', N'VIP');

-- =============== LOẠI MÓN ===============
INSERT INTO LoaiMon VALUES
('LM0001', N'Món khai vị', N'Món ăn nhẹ dùng trước bữa chính'),
('LM0002', N'Món chính', N'Các món hải sản cao cấp'),
('LM0003', N'Tráng miệng', N'Kem, bánh, chè...'),
('LM0004', N'Nước uống', N'Nước ngọt, sinh tố, rượu vang'),
('LM0005', N'Món đặc biệt', N'Món signature của nhà hàng');


-- =============== KHUYẾN MÃI ===============
INSERT INTO KhuyenMai (maKM, tenKM, soLuong, ngayPhatHanh, ngayKetThuc, maThayThe, phanTramGiamGia, uuDai)
VALUES
    ('KM0001', N'Giảm giá khai trương', 100, '2025-01-01', '2025-01-31', NULL, 20, 1),
    ('KM0002', N'Tháng sinh nhật khách hàng', 50, '2025-02-01', '2025-02-28', 'KM001', 15, 1),
    ('KM0003', N'Giảm giá ngày lễ tình nhân', 80, '2025-02-10', '2025-02-20', NULL, 10, 0),
    ('KM0004', N'Khuyến mãi cuối tuần', 200, '2025-03-01', '2025-03-31', NULL, 5, 0),
    ('KM0005', N'Giảm giá hè sôi động', 150, '2025-06-01', '2025-06-30', 'KM004', 25, 1);

-- =============== MÓN ===============
INSERT INTO Mon (maMon, tenMon, moTa, hinhAnh, giaGoc, loaiMon) VALUES
('MN0001', N'Gỏi cua lột', N'Món khai vị hương vị thanh mát', 'goi_cua.jpg', 80000, 'LM0001'),
('MN0002', N'Cua hoàng đế hấp bia', N'Món đặc sản cao cấp', 'cua_hoang_de.jpg', 1200000, 'LM0002'),
('MN0003', N'Chè hạt sen', N'Món tráng miệng mát lạnh', 'che_hat_sen.jpg', 40000, 'LM0003'),
('MN0004', N'Sinh tố xoài', N'Nước uống tươi mát', 'sinh_to_xoai.jpg', 30000, 'LM0004'),
('MN0005', N'Lẩu cua đồng', N'Món đặc biệt cho nhóm đông', 'lau_cua_dong.jpg', 250000, 'LM0005');

-- =============== PHẦN TRĂM GIÁ BÁN ===============
INSERT INTO PhanTramGiaBan (maPTGB, maLoaiMon, phanTramLoi, ngayApDung, maMon) VALUES
('PG0006', 'LM0001', 35, '2025-04-01', 'MN0001'),
('PG0007', 'LM0002', 45, '2025-04-01', 'MN0002'),
('PG0008', 'LM0003', 30, '2025-04-01', 'MN0003'),
('PG0009', 'LM0004', 25, '2025-04-01', 'MN0004'),
('PG0010', 'LM0005', 55, '2025-04-01', 'MN0005'),
('PG0011', 'LM0001', 30, '2025-04-01', NULL),
('PG0012', 'LM0002', 40, '2025-04-01', NULL),
('PG0013', 'LM0003', 25, '2025-04-01', NULL),
('PG0014', 'LM0004', 20, '2025-04-01', NULL),
('PG0015', 'LM0005', 50, '2025-04-01', NULL);

-- =============== SỰ KIỆN ===============
INSERT INTO SuKien VALUES
('SK0001', N'Sinh nhật', N'Trọn gói sinh nhật nhỏ', 200000),
('SK0002', N'Tiệc kỷ niệm', N'Tổ chức kỷ niệm công ty', 300000),
('SK0003', N'Đám cưới', N'Dịch vụ tiệc cưới sang trọng', 500000),
('SK0004', N'Họp mặt bạn bè', N'Không gian riêng tư', 150000),
('SK0005', N'Tiệc Giáng Sinh', N'Sự kiện cuối năm', 250000);

-- =============== THỜI GIAN ĐỔI BÀN ===============
INSERT INTO ThoiGianDoiBan VALUES
('TD0001', 1, 15),
('TD0002', 0, 5);


-- Xóa dữ liệu cũ nếu có hàng NULL
DELETE FROM Coc WHERE maCoc IS NULL;

-- Dữ liệu mới
INSERT INTO Coc (maCoc, loaiCoc, phanTramCoc, soTienCoc, maLoaiBan, maKhuVuc) VALUES
-- Khu vực 1: LB0001 - LB0004
('CO0001', 1, 10,  NULL, 'LB0001', 'KV0001'),
('CO0002', 1, 15,  NULL, 'LB0002', 'KV0001'),
('CO0003', 0, NULL, 200000, 'LB0003', 'KV0001'),
('CO0004', 0, NULL, 300000, 'LB0004', 'KV0001'),

-- Khu vực 2: LB0001 - LB0004
('CO0005', 1, 20,  NULL, 'LB0001', 'KV0002'),
('CO0006', 1, 10,  NULL, 'LB0002', 'KV0002'),
('CO0007', 0, NULL, 250000, 'LB0003', 'KV0002'),
('CO0008', 0, NULL, 400000, 'LB0004', 'KV0002'),

-- Khu vực 3: LB0004 - LB0005
('CO0009', 1, 15,  NULL, 'LB0004', 'KV0003'),
('CO0010', 0, NULL, 500000, 'LB0005', 'KV0003');
---


--==========SAU KHI NHẬP DỮ LIỆU BÀN THÌ MỚI NHẬP ĐƯỢC DỮ LIỆU HÓA ĐƠN



INSERT INTO HoaDon (
    maHD, maKH, maNV, maBan, maKM, maSK,
    tgCheckin, tgCheckout, kieuThanhToan, kieuDatBan,
    thue, coc, trangThai, soLuong,
    tongTienTruoc, tongTienSau, tongTienKM
)
VALUES
    ('HD25102701', 'KH0001', 'NV0001', 'B001', 'KM0001', 'SK0001', '2025-10-27 09:00', '2025-10-27 10:15', 1, 0, 0.10, 50000, 1, 2, 200000, 180000, 20000),
    ('HD25102702', 'KH0002', 'NV0002', 'B002', 'KM0002', 'SK0002', '2025-10-27 10:00', '2025-10-27 11:30', 0, 0, 0.10, 40000, 1, 3, 300000, 270000, 30000),
    ('HD25102703', 'KH0003', 'NV0003', 'B003', 'KM0003', 'SK0003', '2025-10-27 11:00', '2025-10-27 12:00', 1, 1, 0.10, 30000, 1, 1, 120000, 108000, 12000),
    ('HD25102704', 'KH0004', 'NV0004', 'B004', 'KM0004', 'SK0004', '2025-10-27 12:15', '2025-10-27 13:45', 0, 1, 0.10, 60000, 1, 4, 450000, 405000, 45000),
    ('HD25102705', 'KH0005', 'NV0005', 'B005', 'KM0005', 'SK0005', '2025-10-27 14:00', '2025-10-27 15:30', 1, 0, 0.10, 35000, 1, 2, 180000, 162000, 18000),
    ('HD25102706', 'KH0001', 'NV0001', 'B006', 'KM0001', 'SK0001', '2025-10-27 15:45', '2025-10-27 16:50', 0, 0, 0.10, 25000, 1, 3, 260000, 234000, 26000),
    ('HD25102707', 'KH0002', 'NV0002', 'B007', 'KM0002', 'SK0002', '2025-10-27 17:10', '2025-10-27 18:20', 1, 1, 0.10, 20000, 1, 2, 220000, 198000, 22000),
    ('HD25102708', 'KH0003', 'NV0003', 'B008', 'KM0003', 'SK0003', '2025-10-27 18:30', '2025-10-27 19:45', 0, 0, 0.10, 30000, 1, 3, 310000, 279000, 31000),
    ('HD25102709', 'KH0004', 'NV0004', 'B009', 'KM0004', 'SK0004', '2025-10-27 20:00', '2025-10-27 21:10', 1, 1, 0.10, 40000, 1, 4, 400000, 360000, 40000),
    ('HD25102710', 'KH0005', 'NV0005', 'B010', 'KM0005', 'SK0005', '2025-10-27 21:30', '2025-10-27 22:45', 0, 0, 0.10, 45000, 1, 5, 500000, 450000, 50000);