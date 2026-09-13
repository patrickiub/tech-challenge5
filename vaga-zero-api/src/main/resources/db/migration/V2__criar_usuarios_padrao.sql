-- Paciente vinculado ao usuario de demonstracao (Maria Silva)
INSERT INTO paciente (nome, cns, telefone, latitude, longitude, data_nascimento)
VALUES ('Maria da Silva', '700003054441234', '11987654321', -23.561684, -46.655981, '1990-05-20');

-- Usuarios padrao para demonstracao (senha em hash BCrypt)
-- gestor@vagazero.com / senha: gestor123
INSERT INTO usuario (email, senha_hash, perfil, paciente_id)
VALUES ('gestor@vagazero.com', '$2a$10$mRiU.mlxpI.hrtPemhLuS.3hZ4/g7UTu3Hhhc3z3rEq8bN63l1J2a', 'GESTOR', NULL);

-- maria.silva@email.com / senha: paciente123
INSERT INTO usuario (email, senha_hash, perfil, paciente_id)
VALUES ('maria.silva@email.com', '$2a$10$YHnkgAtWP3t.UVp8yBvKV.Tcag9U.bi68POqIUubhe9KlIfdAYzSG', 'PACIENTE',
        (SELECT id FROM paciente WHERE cns = '700003054441234'));
