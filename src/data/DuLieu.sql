USE QL_NhaHangCrabKing_Nhom02;
GO

-- =============== HẠNG KHÁCH HÀNG ===============
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
                         ('NV0005', N'Phạm Gia Huy', '0915666777', 1, 0, '2025-04-01', 1, '1');

-- =============== KHU VỰC ===============
INSERT INTO KhuVuc VALUES
                       ('KV0001', N'Outdoor'),
                       ('KV0002', N'Indoor'),
                       ('KV0003', N'VIP');

-- =============== LOẠI BÀN ===============
INSERT INTO LoaiBan VALUES
                        ('LB0001', N'Loại A', 2),
                        ('LB0002', N'Loại B', 4),
                        ('LB0003', N'Loại C', 8),
                        ('LB0004', N'Loại D', 12),
                        ('LB0005', N'Loại E', 20);

-- =============== LOẠI MÓN ===============
INSERT INTO LoaiMon VALUES
                        ('LM0001', N'Món khai vị', N'Món ăn nhẹ dùng trước bữa chính'),
                        ('LM0002', N'Món chính', N'Món hải sản đặc trưng của nhà hàng'),
                        ('LM0003', N'Món tráng miệng', N'Kem, chè, bánh ngọt...'),
                        ('LM0004', N'Nước uống', N'Nước ngọt, sinh tố, bia rượu...'),
                        ('LM0005', N'Món đặc biệt', N'Món signature - cua hoàng đế, lẩu cua, tôm hùm...');

-- =============== KHUYẾN MÃI ===============
INSERT INTO KhuyenMai (maKM, tenKM, soLuong, ngayPhatHanh, ngayKetThuc, maThayThe, phanTramGiamGia, uuDai)
VALUES
    ('KM0001', N'Giảm giá khai trương', 100, '2025-01-01', '2025-01-31', 'KHAITRUONG', 20, 1),
    ('KM0002', N'Sinh nhật khách hàng', 50, '2025-02-01', '2025-02-28', 'SINHNHAT', 15, 1),
    ('KM0003', N'Giảm giá lễ tình nhân', 80, '2025-02-10', '2025-02-20', 'TINHNHAN', 10, 0),
    ('KM0004', N'Khuyến mãi cuối tuần', 200, '2025-03-01', '2025-03-31', 'CUOITUAN', 5, 0),
    ('KM0005', N'Giảm giá hè sôi động', 150, '2025-06-01', '2025-06-30', 'MUAHE', 25, 1);

-- =============== PHẦN TRĂM GIÁ BÁN ===============
INSERT INTO PhanTramGiaBan VALUES
                               ('PG0001', 'LM0001', NUll, 35, '2025-04-01'),
                               ('PG0002', 'LM0002', NUll, 45, '2025-04-01'),
                               ('PG0003', 'LM0003', NUll, 25, '2025-04-01'),
                               ('PG0004', 'LM0004', NUll, 20, '2025-04-01'),
                               ('PG0005', 'LM0005', NUll, 50, '2025-04-01');


-- =============== SỰ KIỆN ===============
INSERT INTO SuKien VALUES
                       ('SK0001', N'Sinh nhật', N'Gói trang trí & nhạc sinh nhật', 200000),
                       ('SK0002', N'Tiệc kỷ niệm', N'Không gian riêng tư cho công ty', 300000),
                       ('SK0003', N'Đám cưới', N'Dịch vụ tiệc cưới sang trọng', 500000),
                       ('SK0004', N'Họp mặt bạn bè', N'Không gian ấm cúng, nhẹ nhàng', 150000),
                       ('SK0005', N'Tiệc Giáng Sinh', N'Sự kiện đặc biệt cuối năm', 250000);

-- =============== THỜI GIAN ĐỔI BÀN ===============
INSERT INTO ThoiGianDoiBan VALUES
                               ('TD0001', 1, 15),  -- Đặt ăn liền: cho phép đổi trong 15 phút
                               ('TD0002', 0, 5);   -- Đặt trước: cho phép đổi trong 5 phút

-- =============== CỌC ===============

INSERT INTO Coc VALUES
-- KV0001 - Outdoor
('CO0001', 1, 10,  0, 'LB0001', 'KV0001'),
('CO0002', 1, 15,  0, 'LB0002', 'KV0001'),
('CO0003', 0, 0, 200000, 'LB0003', 'KV0001'),
('CO0004', 0, 0, 300000, 'LB0004', 'KV0001'),

-- KV0002 - Indoor
('CO0005', 1, 20,  0, 'LB0001', 'KV0002'),
('CO0006', 1, 10,  0, 'LB0002', 'KV0002'),
('CO0007', 0, 0, 250000, 'LB0003', 'KV0002'),
('CO0008', 0, 0, 400000, 'LB0004', 'KV0002'),

-- KV0003 - VIP
('CO0009', 1, 15, 0, 'LB0004', 'KV0003'),
('CO0010', 0, 0, 500000, 'LB0005', 'KV0003');
delete from Coc
---==== Mon=======
                USE QL_NhaHangCrabKing_Nhom02;

delete from Mon

    INSERT INTO Mon VALUES
    ('MM0001', N'Gỏi cua lột', N'Món khai vị tươi mát, vị chua ngọt', 'goi_cua_lot.jpg', 85000, 20, 'LM0001'),
    ('MM0002', N'Chả giò hải sản', N'Cuốn giòn rụm, nhân tôm mực', 'cha_gio_hai_san.jpg', 75000, 25, 'LM0001'),
    ('MM0003', N'Súp cua trứng bách thảo', N'Súp nóng hổi, thơm mùi cua', 'sup_cua_trung.jpg', 65000, 30, 'LM0001'),
    ('MM0004', N'Nghêu hấp sả', N'Nghêu tươi hấp sả gừng, thanh vị', 'ngheu_hap_sa.jpg', 70000, 28, 'LM0001'),
    ('MM0005', N'Hàu nướng mỡ hành', N'Hàu tươi nướng thơm béo', 'hau_nuong_mo_hanh.jpg', 90000, 15, 'LM0001'),
    ('MM0006', N'Mực chiên giòn', N'Mực tươi tẩm bột chiên vàng', 'muc_chien_gion.jpg', 85000, 22, 'LM0001'),
    ('MM0007', N'Càng ghẹ rang muối', N'Càng ghẹ rang giòn, vị đậm đà', 'cang_ghe_muoi.jpg', 95000, 18, 'LM0001'),
    ('MM0008', N'Khoai tây chiên', N'Món ăn nhẹ giòn rụm', 'khoai_tay_chien.jpg', 55000, 35, 'LM0001'),
    ('MM0009', N'Salad rong biển trứng cua', N'Món salad thanh mát, vị lạ miệng', 'salad_rong_bien.jpg', 78000, 25, 'LM0001'),
    ('MM0010', N'Bạch tuộc nướng sa tế', N'Món khai vị cay nhẹ, thơm nức', 'bach_tuoc_sate.jpg', 98000, 20, 'LM0001'),
    ('MM0011', N'Súp hải sản chua cay', N'Vị Thái cay chua hấp dẫn', 'sup_hai_san_thai.jpg', 72000, 18, 'LM0001'),
    ('MM0012', N'Gỏi ngó sen tôm thịt', N'Món truyền thống, vị giòn và thanh', 'goi_ngo_sen.jpg', 78000, 24, 'LM0001'),
    ('MM0013', N'Bánh mì nướng bơ tỏi', N'Ăn kèm món chính rất hợp', 'banh_mi_bo_toi.jpg', 45000, 40, 'LM0001'),
    ('MM0014', N'Chả ốc nướng lá lốt', N'Hương vị đặc trưng Việt Nam', 'cha_oc_la_lot.jpg', 80000, 18, 'LM0001'),
    ('MM0015', N'Sò điệp nướng phô mai', N'Món nướng béo thơm, cực hấp dẫn', 'so_diep_pho_mai.jpg', 95000, 15, 'LM0001'),
    ('MM0016', N'Cánh gà chiên mắm', N'Vị mặn ngọt đậm đà', 'canh_ga_chien_mam.jpg', 75000, 20, 'LM0001'),
    ('MM0017', N'Salad trộn dầu giấm', N'Món nhẹ, chống ngán', 'salad_dau_giam.jpg', 60000, 28, 'LM0001'),
    ('MM0018', N'Súp bí đỏ kem tôm', N'Súp mịn béo, vị tôm ngọt', 'sup_bi_do_tom.jpg', 70000, 26, 'LM0001'),
    ('MM0019', N'Gỏi xoài khô cá sặc', N'Vị chua cay mặn ngọt hấp dẫn', 'goi_xoai_ca_sac.jpg', 75000, 22, 'LM0001'),
    ('MM0020', N'Hàu sống chanh muối', N'Hàu tươi ăn cùng chanh ớt', 'hau_song_anh.jpg', 95000, 10, 'LM0001');

INSERT INTO Mon VALUES
                    ('MM0021', N'Cua hoàng đế hấp bia', N'Món chính sang trọng, thịt ngọt', 'cua_hoang_de.jpg', 1250000, 10, 'LM0002'),
                    ('MM0022', N'Lẩu hải sản Thái Lan', N'Chua cay hấp dẫn', 'lau_hai_san_thai.jpg', 350000, 20, 'LM0002'),
                    ('MM0023', N'Cơm chiên hải sản', N'Món chính no bụng', 'com_chien_hai_san.jpg', 95000, 25, 'LM0002'),
                    ('MM0024', N'Mì xào hải sản', N'Mì dai, tôm mực tươi', 'mi_xao_hai_san.jpg', 85000, 22, 'LM0002'),
                    ('MM0025', N'Ghẹ rang me', N'Vị chua ngọt kích thích vị giác', 'ghe_rang_me.jpg', 99000, 18, 'LM0002'),
                    ('MM0026', N'Tôm sú nướng muối ớt', N'Tôm tươi nướng cay nhẹ', 'tom_su_muoi_ot.jpg', 98000, 20, 'LM0002'),
                    ('MM0027', N'Mực nhồi thịt hấp', N'Món nóng, mềm thơm', 'muc_nhoi_thit.jpg', 110000, 15, 'LM0002'),
                    ('MM0028', N'Cua rang muối HongKong', N'Cua giòn vị mặn nhẹ', 'cua_muoi_hk.jpg', 1150000, 8, 'LM0002'),
                    ('MM0029', N'Cá chẽm sốt chanh dây', N'Sốt chua ngọt đặc biệt', 'ca_chem_chanh_day.jpg', 180000, 12, 'LM0002'),
                    ('MM0030', N'Lẩu riêu cua bắp bò', N'Món lẩu đậm vị Bắc', 'lau_rieu_cua.jpg', 280000, 10, 'LM0002'),
                    ('MM0031', N'Cá hồi nướng bơ tỏi', N'Thịt cá béo mềm, thơm lừng', 'ca_hoi_bo_toi.jpg', 220000, 15, 'LM0002'),
                    ('MM0032', N'Tôm càng xanh hấp bia', N'Tôm to ngọt thịt', 'tom_cang_xanh.jpg', 270000, 12, 'LM0002'),
                    ('MM0033', N'Cơm chiên trứng cua', N'Món cơm vàng thơm', 'com_chien_trung_cua.jpg', 90000, 30, 'LM0002'),
                    ('MM0034', N'Hàu nướng phô mai', N'Béo thơm nồng nàn', 'hau_pho_mai.jpg', 98000, 20, 'LM0002'),
                    ('MM0035', N'Lẩu tôm hùm mini', N'Sang trọng, thơm ngon', 'lau_tom_hum.jpg', 550000, 8, 'LM0002'),
                    ('MM0036', N'Cá mú hấp xì dầu', N'Món Trung Hoa tinh tế', 'ca_mu_xi_dau.jpg', 250000, 10, 'LM0002'),
                    ('MM0037', N'Cá basa kho tộ', N'Món truyền thống Việt Nam', 'ca_basa_kho_to.jpg', 85000, 25, 'LM0002'),
                    ('MM0038', N'Ghẹ hấp bia', N'Tươi ngon, đậm đà', 'ghe_hap_bia.jpg', 120000, 20, 'LM0002'),
                    ('MM0039', N'Tôm chiên bột', N'Món giòn rụm, trẻ em thích', 'tom_chien_bot.jpg', 95000, 18, 'LM0002'),
                    ('MM0040', N'Cá hồi áp chảo', N'Món healthy, ngon nhẹ', 'ca_hoi_ap_chao.jpg', 210000, 14, 'LM0002'),
                    ('MM0041', N'Lẩu cua đồng', N'Món đậm vị miền Tây', 'lau_cua_dong.jpg', 260000, 15, 'LM0002'),
                    ('MM0042', N'Mì Ý sốt cua', N'Mì Ý fusion phong cách Việt', 'mi_y_sot_cua.jpg', 120000, 18, 'LM0002'),
                    ('MM0043', N'Tôm nướng bơ tỏi', N'Món thơm nức, ai cũng mê', 'tom_bo_toi.jpg', 95000, 20, 'LM0002'),
                    ('MM0044', N'Cá thu sốt cà', N'Món dân dã ngon cơm', 'ca_thu_sot_ca.jpg', 85000, 22, 'LM0002'),
                    ('MM0045', N'Cua sốt Singapore', N'Món đặc trưng vị cay nồng', 'cua_singapore.jpg', 1100000, 10, 'LM0002'),
                    ('MM0046', N'Mực nướng ngũ vị', N'Món hải sản nướng độc đáo', 'muc_ngu_vi.jpg', 105000, 15, 'LM0002'),
                    ('MM0047', N'Cơm tôm rim mặn', N'Món cơm đậm đà hương vị', 'com_tom_rim.jpg', 88000, 25, 'LM0002'),
                    ('MM0048', N'Thịt ba rọi nướng riềng', N'Kết hợp hải vị và thịt Việt', 'ba_roi_rieng.jpg', 90000, 20, 'LM0002'),
                    ('MM0049', N'Cua rang tiêu đen', N'Cua tươi rang cay nhẹ', 'cua_rang_tieu.jpg', 1150000, 8, 'LM0002'),
                    ('MM0050', N'Mì xào thập cẩm', N'Món kết hợp đa dạng hải sản', 'mi_xao_thap_cam.jpg', 95000, 22, 'LM0002');

INSERT INTO Mon VALUES
                    ('MM0051', N'Chè hạt sen', N'Món tráng miệng thanh mát', 'che_hat_sen.jpg', 45000, 30, 'LM0003'),
                    ('MM0052', N'Kem vani', N'Kem ngọt nhẹ, mát lạnh', 'kem_vani.jpg', 40000, 25, 'LM0003'),
                    ('MM0053', N'Bánh flan', N'Mềm, béo, thơm caramel', 'banh_flan.jpg', 35000, 35, 'LM0003'),
                    ('MM0054', N'Chè khúc bạch', N'Món tráng miệng nổi tiếng', 'che_khuc_bach.jpg', 48000, 28, 'LM0003'),
                    ('MM0055', N'Sâm bổ lượng', N'Thanh mát, bổ dưỡng', 'sam_bo_luong.jpg', 50000, 20, 'LM0003'),
                    ('MM0056', N'Rau câu dừa', N'Món mát lạnh, ngon miệng', 'rau_cau_dua.jpg', 40000, 30, 'LM0003'),
                    ('MM0057', N'Kem xoài', N'Kem trái cây nhiệt đới', 'kem_xoai.jpg', 42000, 25, 'LM0003'),
                    ('MM0058', N'Chè thái', N'Ngọt mát, nhiều topping', 'che_thai.jpg', 48000, 25, 'LM0003'),
                    ('MM0059', N'Pudding dâu', N'Món mềm béo, vị dâu nhẹ', 'pudding_dau.jpg', 45000, 20, 'LM0003'),
                    ('MM0060', N'Chuối nướng nước cốt dừa', N'Món dân dã đặc sản Nam Bộ', 'chuoi_nuong.jpg', 55000, 20, 'LM0003'),
                    ('MM0061', N'Kem sầu riêng', N'Béo thơm đậm đà', 'kem_sau_rieng.jpg', 48000, 18, 'LM0003'),
                    ('MM0062', N'Chè đậu xanh đánh', N'Món truyền thống thanh nhẹ', 'che_dau_xanh.jpg', 42000, 22, 'LM0003'),
                    ('MM0063', N'Bánh crepe sầu riêng', N'Mềm thơm, vị độc đáo', 'crepe_sau_rieng.jpg', 55000, 15, 'LM0003'),
                    ('MM0064', N'Kem dừa xiêm', N'Món được yêu thích nhất hè', 'kem_dua_xiem.jpg', 48000, 20, 'LM0003'),
                    ('MM0065', N'Chè bưởi', N'Ngọt nhẹ, giòn thơm', 'che_buoi.jpg', 45000, 28, 'LM0003'),
                    ('MM0066', N'Bánh plan trân châu', N'Sự kết hợp mới lạ', 'plan_tran_chau.jpg', 50000, 15, 'LM0003'),
                    ('MM0067', N'Chè trái cây', N'Tươi mát, nhiều vitamin', 'che_trai_cay.jpg', 45000, 30, 'LM0003'),
                    ('MM0068', N'Kem socola', N'Ngọt đắng cân bằng', 'kem_socola.jpg', 45000, 25, 'LM0003'),
                    ('MM0069', N'Rau câu cafe', N'Món tráng miệng hiện đại', 'rau_cau_cafe.jpg', 40000, 22, 'LM0003'),
                    ('MM0070', N'Chè thập cẩm', N'Món Việt quen thuộc', 'che_thap_cam.jpg', 45000, 28, 'LM0003');

INSERT INTO Mon VALUES
                    ('MM0071', N'Nước suối', N'Nước tinh khiết đóng chai', 'nuoc_suoi.jpg', 15000, 60, 'LM0004'),
                    ('MM0072', N'Coca Cola', N'Nước ngọt có gas', 'coca.jpg', 20000, 50, 'LM0004'),
                    ('MM0073', N'Pepsi', N'Nước ngọt phổ biến', 'pepsi.jpg', 20000, 50, 'LM0004'),
                    ('MM0074', N'Sprite', N'Nước chanh có gas', 'sprite.jpg', 20000, 40, 'LM0004'),
                    ('MM0075', N'Trà đào cam sả', N'Món uống hot trend', 'tra_dao_cam_sa.jpg', 45000, 30, 'LM0004'),
                    ('MM0076', N'Sinh tố xoài', N'Thơm ngon, mát lạnh', 'sinh_to_xoai.jpg', 40000, 25, 'LM0004'),
                    ('MM0077', N'Sinh tố bơ', N'Béo ngậy, thơm ngon', 'sinh_to_bo.jpg', 42000, 25, 'LM0004'),
                    ('MM0078', N'Soda chanh', N'Giải khát sảng khoái', 'soda_chanh.jpg', 35000, 35, 'LM0004'),
                    ('MM0079', N'Soda việt quất', N'Ngon, mát, đẹp mắt', 'soda_viet_quat.jpg', 38000, 30, 'LM0004'),
                    ('MM0080', N'Nước cam ép', N'Bổ sung vitamin C', 'nuoc_cam_ep.jpg', 40000, 30, 'LM0004'),
                    ('MM0081', N'Trà tắc mật ong', N'Thanh mát, dễ uống', 'tra_tac_mat_ong.jpg', 35000, 35, 'LM0004'),
                    ('MM0082', N'Sinh tố dâu', N'Món được yêu thích', 'sinh_to_dau.jpg', 42000, 25, 'LM0004'),
                    ('MM0083', N'Nước ép dưa hấu', N'Mát lạnh ngày hè', 'ep_dua_hau.jpg', 40000, 25, 'LM0004'),
                    ('MM0084', N'Nước ép cà rốt', N'Tốt cho sức khỏe, vị ngọt tự nhiên', 'ep_ca_rot.jpg', 40000, 25, 'LM0004'),
                    ('MM0085', N'Nước ép táo', N'Tươi ngon, thanh vị', 'ep_tao.jpg', 40000, 25, 'LM0004'),
                    ('MM0086', N'Nước ép dứa', N'Ngọt dịu, tốt cho tiêu hóa', 'ep_dua.jpg', 38000, 25, 'LM0004'),
                    ('MM0087', N'Cà phê đen đá', N'Cà phê đậm đà truyền thống', 'ca_phe_den.jpg', 30000, 35, 'LM0004'),
                    ('MM0088', N'Cà phê sữa đá', N'Món quen thuộc, béo thơm', 'ca_phe_sua.jpg', 35000, 30, 'LM0004'),
                    ('MM0089', N'Sinh tố dưa gang', N'Mát lạnh, thơm ngọt', 'sinh_to_dua_gang.jpg', 42000, 20, 'LM0004'),
                    ('MM0090', N'Trà sữa trân châu', N'Món uống hot trend giới trẻ', 'tra_sua_tran_chau.jpg', 48000, 30, 'LM0004');

INSERT INTO Mon VALUES
                    ('MM0091', N'Tôm hùm nướng bơ tỏi', N'Món cao cấp được ưa chuộng', 'tom_hum_bo_toi.jpg', 950000, 10, 'LM0005'),
                    ('MM0092', N'Lẩu cua hoàng đế', N'Món signature đặc biệt của CrabKing', 'lau_cua_hoang_de.jpg', 1500000, 5, 'LM0005'),
                    ('MM0093', N'Cua Alaska hấp bia', N'Món đẳng cấp, thịt ngọt mềm', 'cua_alaska.jpg', 1800000, 4, 'LM0005'),
                    ('MM0094', N'Cua sốt trứng muối', N'Món độc quyền siêu đỉnh', 'cua_trung_muoi.jpg', 1200000, 8, 'LM0005'),
                    ('MM0095', N'Lẩu tôm hùm đặc biệt', N'Món ăn sang trọng, đậm đà', 'lau_tom_hum_dac_biet.jpg', 1300000, 6, 'LM0005'),
                    ('MM0096', N'Cua rang me CrabKing', N'Hương vị độc quyền của nhà hàng', 'cua_rang_me_ck.jpg', 1100000, 10, 'LM0005'),
                    ('MM0097', N'Tôm càng xanh sốt phô mai', N'Món hải sản béo thơm', 'tom_cang_pho_mai.jpg', 550000, 8, 'LM0005'),
                    ('MM0098', N'Súp bào ngư vi cá', N'Món thượng hạng cho thực khách VIP', 'sup_bao_ngu_vi_ca.jpg', 900000, 6, 'LM0005'),
                    ('MM0099', N'Cua King Crab sốt Singapore', N'Món biểu tượng của nhà hàng', 'kingcrab_singapore.jpg', 1400000, 5, 'LM0005'),
                    ('MM0100', N'Tôm hùm hấp rượu vang', N'Món kết hợp hương vị châu Âu tinh tế', 'tom_hum_ruou_vang.jpg', 1600000, 4, 'LM0005');


--===Ban===
INSERT INTO Ban (maBan, trangThai, maLoaiBan, maKhuVuc) VALUES
-- Loại A
('BO0001', 0, 'LB0001', 'KV0001'),
('BO0002', 0, 'LB0001', 'KV0001'),
('BO0003', 0, 'LB0001', 'KV0001'),
('BO0004', 0, 'LB0001', 'KV0001'),
('BO0005', 0, 'LB0001', 'KV0001'),
('BO0006', 0, 'LB0001', 'KV0001'),
('BO0007', 0, 'LB0001', 'KV0001'),
('BO0008', 0, 'LB0001', 'KV0001'),
('BO0009', 0, 'LB0001', 'KV0001'),
('BO0010', 0, 'LB0001', 'KV0001'),
('BO0011', 0, 'LB0001', 'KV0001'),
('BO0012', 0, 'LB0001', 'KV0001'),

-- Loại B
('BO0013', 0, 'LB0002', 'KV0001'),
('BO0014', 0, 'LB0002', 'KV0001'),
('BO0015', 0, 'LB0002', 'KV0001'),
('BO0016', 0, 'LB0002', 'KV0001'),
('BO0017', 0, 'LB0002', 'KV0001'),
('BO0018', 0, 'LB0002', 'KV0001'),
('BO0019', 0, 'LB0002', 'KV0001'),
('BO0020', 0, 'LB0002', 'KV0001'),
('BO0021', 0, 'LB0002', 'KV0001'),
('BO0022', 0, 'LB0002', 'KV0001'),
('BO0023', 0, 'LB0002', 'KV0001'),
('BO0024', 0, 'LB0002', 'KV0001'),
('BO0025', 0, 'LB0002', 'KV0001'),
('BO0026', 0, 'LB0002', 'KV0001'),
('BO0027', 0, 'LB0002', 'KV0001'),
('BO0028', 0, 'LB0002', 'KV0001'),

-- Loại C
('BO0029', 0, 'LB0003', 'KV0001'),
('BO0030', 0, 'LB0003', 'KV0001'),
('BO0031', 0, 'LB0003', 'KV0001'),
('BO0032', 0, 'LB0003', 'KV0001'),
('BO0033', 0, 'LB0003', 'KV0001'),
('BO0034', 0, 'LB0003', 'KV0001'),
('BO0035', 0, 'LB0003', 'KV0001'),
('BO0036', 0, 'LB0003', 'KV0001'),
('BO0037', 0, 'LB0003', 'KV0001'),
('BO0038', 0, 'LB0003', 'KV0001'),

-- Loại D
('BO0039', 0, 'LB0004', 'KV0001'),
('BO0040', 0, 'LB0004', 'KV0001');
GO

------------------------------------------------------------
-- ===== KHU IN (KV0002) =====
-- 10 bàn loại A, 20 bàn loại B, 15 bàn loại C, 5 bàn loại D
------------------------------------------------------------
INSERT INTO Ban (maBan, trangThai, maLoaiBan, maKhuVuc) VALUES
-- Loại A
('BI0001', 0, 'LB0001', 'KV0002'),
('BI0002', 0, 'LB0001', 'KV0002'),
('BI0003', 0, 'LB0001', 'KV0002'),
('BI0004', 0, 'LB0001', 'KV0002'),
('BI0005', 0, 'LB0001', 'KV0002'),
('BI0006', 0, 'LB0001', 'KV0002'),
('BI0007', 0, 'LB0001', 'KV0002'),
('BI0008', 0, 'LB0001', 'KV0002'),
('BI0009', 0, 'LB0001', 'KV0002'),
('BI0010', 0, 'LB0001', 'KV0002'),

-- Loại B
('BI0011', 0, 'LB0002', 'KV0002'),
('BI0012', 0, 'LB0002', 'KV0002'),
('BI0013', 0, 'LB0002', 'KV0002'),
('BI0014', 0, 'LB0002', 'KV0002'),
('BI0015', 0, 'LB0002', 'KV0002'),
('BI0016', 0, 'LB0002', 'KV0002'),
('BI0017', 0, 'LB0002', 'KV0002'),
('BI0018', 0, 'LB0002', 'KV0002'),
('BI0019', 0, 'LB0002', 'KV0002'),
('BI0020', 0, 'LB0002', 'KV0002'),
('BI0021', 0, 'LB0002', 'KV0002'),
('BI0022', 0, 'LB0002', 'KV0002'),
('BI0023', 0, 'LB0002', 'KV0002'),
('BI0024', 0, 'LB0002', 'KV0002'),
('BI0025', 0, 'LB0002', 'KV0002'),
('BI0026', 0, 'LB0002', 'KV0002'),
('BI0027', 0, 'LB0002', 'KV0002'),
('BI0028', 0, 'LB0002', 'KV0002'),
('BI0029', 0, 'LB0002', 'KV0002'),
('BI0030', 0, 'LB0002', 'KV0002'),

-- Loại C
('BI0031', 0, 'LB0003', 'KV0002'),
('BI0032', 0, 'LB0003', 'KV0002'),
('BI0033', 0, 'LB0003', 'KV0002'),
('BI0034', 0, 'LB0003', 'KV0002'),
('BI0035', 0, 'LB0003', 'KV0002'),
('BI0036', 0, 'LB0003', 'KV0002'),
('BI0037', 0, 'LB0003', 'KV0002'),
('BI0038', 0, 'LB0003', 'KV0002'),
('BI0039', 0, 'LB0003', 'KV0002'),
('BI0040', 0, 'LB0003', 'KV0002'),
('BI0041', 0, 'LB0003', 'KV0002'),
('BI0042', 0, 'LB0003', 'KV0002'),
('BI0043', 0, 'LB0003', 'KV0002'),
('BI0044', 0, 'LB0003', 'KV0002'),
('BI0045', 0, 'LB0003', 'KV0002'),

-- Loại D
('BI0046', 0, 'LB0004', 'KV0002'),
('BI0047', 0, 'LB0004', 'KV0002'),
('BI0048', 0, 'LB0004', 'KV0002'),
('BI0049', 0, 'LB0004', 'KV0002'),
('BI0050', 0, 'LB0004', 'KV0002');
GO

------------------------------------------------------------
-- ===== KHU VIP (KV0003) =====
-- 7 bàn loại D, 3 bàn loại E
------------------------------------------------------------
INSERT INTO Ban (maBan, trangThai, maLoaiBan, maKhuVuc) VALUES
('BV0001', 0, 'LB0004', 'KV0003'),
('BV0002', 0, 'LB0004', 'KV0003'),
('BV0003', 0, 'LB0004', 'KV0003'),
('BV0004', 0, 'LB0004', 'KV0003'),
('BV0005', 0, 'LB0004', 'KV0003'),
('BV0006', 0, 'LB0004', 'KV0003'),
('BV0007', 0, 'LB0004', 'KV0003'),
('BV0008', 0, 'LB0005', 'KV0003'),
('BV0009', 0, 'LB0005', 'KV0003'),
('BV0010', 0, 'LB0005', 'KV0003');
GO
---====HoaDon=====
USE QL_NhaHangCrabKing_Nhom02;
GO

-- 10 HÓA ĐƠN MẪU
-- 10 HÓA ĐƠN MẪU
INSERT INTO HoaDon (maHD, maKH, maNV, maBan, maKM, maSK, tgCheckin, tgCheckout, kieuThanhToan, kieuDatBan, trangThai, soLuong, moTa) VALUES
('HD10501250001', 'KH0001', 'NV0001', 'BO0001', 'KM0001', 'SK0001', '2025-01-05 11:30', '2025-01-05 12:45', 0, 0, 0, 2, N'Khách ăn trưa, gói sự kiện sinh nhật nhỏ'),
('HD10211250001', 'KH0002', 'NV0002', 'BI0012', 'KM0002', NULL, '2025-02-14 18:00', '2025-02-14 20:15', 1, 0, 0, 4, N'Đặt tiệc Valentine, thanh toán chuyển khoản'),
('HD00310250001', 'KH0003', 'NV0003', 'BV0008', NULL, 'SK0003', '2025-03-10 19:00', '2025-03-10 22:00', 0, 1, 0, 10, N'Khách VIP, sử dụng phòng VIP, checkin trực tiếp'),
('HD00303250001', 'KH0004', 'NV0004', 'BI0035', 'KM0004', NULL, '2025-03-22 12:00', '2025-03-22 13:30', 0, 0, 2, 3, N'Khách đoàn nhỏ, khuyến mãi cuối tuần áp dụng'),
('HD10410250001', 'KH0005', 'NV0005', 'BO0020', NULL, NULL, '2025-04-10 17:30', '2025-04-10 18:50', 0, 0, 2, 2, N'Khách lẻ, không dùng KM/SK'),
('HD10611250001', 'KH0003', 'NV0002', 'BV0001', 'KM0005', 'SK0001', '2025-06-15 18:30', '2025-06-15 21:00', 1, 0, 1, 8, N'Sinh nhật kết hợp khuyến mãi hè'),
('HD00705250001', 'KH0001', 'NV0001', 'BI0046', NULL, NULL, '2025-07-05 11:00', '2025-07-05 12:10', 0, 0, 1, 2, N'Ăn trưa gấp, món nhẹ'),
('HD10202250001', 'KH0004', 'NV0003', 'BO0030', 'KM0002', NULL, '2025-02-20 19:30', '2025-02-20 21:00', 0, 1, 1, 5, N'Khách dùng KM sinh nhật'),
('HD10512250001', 'KH0002', 'NV0004', 'BI0015', NULL, 'SK0004', '2025-05-12 18:00', '2025-05-12 20:00', 0, 0, 1, 6, N'Tiệc họp mặt bạn bè'),
('HD00208250002', 'KH0005', 'NV0005', 'BV0009', 'KM0003', NULL, '2025-02-14 20:00', '2025-02-14 22:30', 0, 0, 0, 4, N'Đêm lễ Tình nhân, sử dụng KM lễ tình nhân');
GO

-- CHI TIẾT HÓA ĐƠN CHO 10 HÓA ĐƠN  (mỗi hóa đơn 2-4 món)
INSERT INTO ChiTietHoaDon (maHD, maMon, soLuong) VALUES
-- HD0001
('HD10501250001', 'MM0002', 2),  -- Chả giò hải sản x2
('HD10501250001', 'MM0033', 1),  -- Cơm chiên trứng cua x1

-- HD0002
('HD10211250001', 'MM0021', 1),  -- Cua hoàng đế hấp bia x1
('HD10211250001', 'MM0022', 1),  -- Lẩu hải sản Thái Lan x1
('HD10211250001', 'MM0075', 4),  -- Trà đào cam sả x4

-- HD0003
('HD00310250001', 'MM0099', 1),  -- Cua King Crab sốt Singapore x1
('HD00310250001', 'MM0088', 6),  -- Cà phê sữa đá x6
('HD00310250001', 'MM0092', 1),  -- Lẩu cua hoàng đế x1

-- HD0004
('HD00303250001', 'MM0030', 1),  -- Lẩu riêu cua bắp bò x1
('HD00303250001', 'MM0033', 2),  -- Cơm chiên trứng cua x2

-- HD0005
('HD10410250001', 'MM0008', 1),  -- Khoai tây chiên x1
('HD10410250001', 'MM0071', 2),  -- Nước suối x2

-- HD0006
('HD10611250001', 'MM0091', 1),  -- Tôm hùm nướng bơ tỏi x1
('HD10611250001', 'MM0056', 2),  -- Rau câu dừa x2
('HD10611250001', 'MM0087', 8),  -- Cà phê đen đá x8

-- HD0007
('HD00705250001', 'MM0013', 2),  -- Bánh mì nướng bơ tỏi x2
('HD00705250001', 'MM0043', 1),  -- Tôm nướng bơ tỏi x1

-- HD0008
('HD10202250001', 'MM0005', 3),  -- Hàu nướng mỡ hành x3
('HD10202250001', 'MM0025', 2),  -- Ghẹ rang me x2

-- HD0009
('HD10512250001', 'MM0001', 2),  -- Gỏi cua lột x2
('HD10512250001', 'MM0079', 6),  -- Soda việt quất x6
('HD10512250001', 'MM0038', 1),  -- Ghẹ hấp bia x1

-- HD0010
('HD00208250002', 'MM0044', 2),  -- Cá thu sốt cà x2
('HD00208250002', 'MM0095', 1);  -- Lẩu tôm hùm đặc biệt x1
GO
