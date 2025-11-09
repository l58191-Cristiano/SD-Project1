-- Limpar tabelas antigas (se existirem) para começar do zero
-- NOTA: A ordem é importante devido as Foreign Keys
DELETE
FROM Partidas;
DELETE
FROM Inscricoes;
DELETE
FROM Torneios;
DELETE
FROM Jogadores;

-- Reiniciar as sequências dos IDs para comecarem em 1
-- Usando pg_get_serial_sequence para encontrar o nome correto da sequência
SELECT setval(pg_get_serial_sequence('jogadores', 'id_jogador'), 1, false);
SELECT setval(pg_get_serial_sequence('torneios', 'id_torneio'), 1, false);
SELECT setval(pg_get_serial_sequence('partidas', 'id_partida'), 1, false);
SELECT setval(pg_get_serial_sequence('inscricoes', 'id_inscricao'), 1, false);

-- 1. CRIAR 12 JOGADORES
INSERT INTO Jogadores (nome, rating, email, clube, estado_admin, estado_geral)
VALUES ('Ana Silva', 1600, 'ana@mail.com', 'Xadrez Lisboa', 'Aprovado', 'Inscrito'),
       ('Bruno Martins', 1550, 'bruno@mail.com', 'Xadrez Porto', 'Aprovado', 'Inscrito'),
       ('Carla Dias', 1620, 'carla@mail.com', 'Clube Évora', 'Aprovado', 'Em Jogo'),
       ('Diogo Vaz', 1400, 'diogo@mail.com', 'Xadrez Faro', 'Aprovado', 'Em Jogo'),
       ('Elsa Moreira', 1700, 'elsa@mail.com', 'Academia Gaia', 'Aprovado', 'Eliminado'),
       ('Fábio Pinto', 1650, 'fabio@mail.com', 'Xadrez Lisboa', 'Aprovado', 'Inscrito'),
       ('Gabriela Matos', 1580, 'gabi@mail.com', 'Xadrez Porto', 'Aprovado', 'Inscrito'),
       ('Hugo Reis', 1750, 'hugo@mail.com', 'Clube Évora', 'Aprovado', 'Inscrito'),
       ('Inês Lopes', 1450, 'ines@mail.com', 'Xadrez Faro', 'Aprovado', 'Inscrito'),
       ('João Neves', 1500, 'joao@mail.com', 'Academia Gaia', 'Aprovado', 'Inscrito'),
       ('Luis Pereira', 1300, 'luis@mail.com', 'Sem Clube', 'Não Aprovado', 'Inscrito'),
       ('Maria Costa', 1350, 'maria@mail.com', 'Sem Clube', 'Não Aprovado', 'Inscrito');

-- 2. CRIAR 4 TORNEIOS
INSERT INTO Torneios (nome, data, local, premio, estado_torneio, estado_admin)
VALUES ('Aberto de Évora', '2025-12-01', 'Évora', 1000, 'Agendado', 'Aprovado'),
       ('Torneio de Verão Porto', '2025-07-15', 'Porto', 1500, 'Ativo', 'Aprovado'),
       ('Clássico de Lisboa', '2025-01-10', 'Lisboa', 2000, 'Concluído', 'Aprovado'),
       ('Torneio Regional Faro', '2026-02-01', 'Faro', 500, 'Agendado', 'Não Aprovado');
-- 3. CRIAR INSCRICOES
INSERT INTO Inscricoes (id_jogador, id_torneio)
VALUES (1, 1),
       (2, 1),
       (3, 1),
       (4, 1),
       (5, 1),
       (6, 1),
       (7, 1),
       (8, 1),
       (1, 2),
       (2, 2),
       (3, 2),
       (4, 2),
       (5, 2),
       (6, 2),
       (7, 2),
       (8, 2),
       (1, 3),
       (2, 3),
       (3, 3),
       (4, 3),
       (5, 3),
       (6, 3),
       (7, 3),
       (8, 3),
       (9, 1);


-- 4. CRIAR 16 PARTIDAS (4 por torneio 'Aprovado')

-- Torneio 1 (Agendado) - 4 partidas agendadas
INSERT INTO Partidas (id_torneio, id_jogador_1, id_jogador_2, estado_partida, ganhador)
VALUES (1, 1, 2, 'Agendado', NULL),
       (1, 3, 4, 'Agendado', NULL),
       (1, 5, 6, 'Agendado', NULL),
       (1, 7, 8, 'Agendado', NULL);

-- Torneio 2 (Ativo) - 2 Encerradas, 2 a Decorrer
INSERT INTO Partidas (id_torneio, id_jogador_1, id_jogador_2, estado_partida, ganhador)
VALUES (2, 1, 3, 'Encerrado', 1), -- Jogador 1 ganhou
       (2, 2, 4, 'Encerrado', 4), -- Jogador 4 ganhou
       (2, 5, 7, 'Decorrer', NULL),
       (2, 6, 8, 'Decorrer', NULL);

-- Torneio 3 (Concluído) - 4 partidas Encerradas
INSERT INTO Partidas (id_torneio, id_jogador_1, id_jogador_2, estado_partida, ganhador)
VALUES (3, 1, 4, 'Encerrado', 4),
       (3, 2, 3, 'Encerrado', 2),
       (3, 5, 8, 'Encerrado', 5),
       (3, 6, 7, 'Encerrado', 7);

-- Torneio 4 (Não Aprovado) - Sem partidas

SELECT 'Dados de teste inseridos com sucesso!' AS Status;