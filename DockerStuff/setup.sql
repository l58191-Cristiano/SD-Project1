CREATE TABLE Jogadores
(
    id_jogador   SERIAL PRIMARY KEY,
    nome         VARCHAR(255) NOT NULL,
    rating       INT          NOT NULL DEFAULT 800,
    email        VARCHAR(255) NOT NULL,
    clube        VARCHAR(255) NOT NULL,
    estado_admin VARCHAR(20)  NOT NULL DEFAULT 'Não Aprovado',
    estado_geral VARCHAR(20)  NOT NULL DEFAULT 'Inscrito'
);

CREATE TABLE Torneios
(
    id_torneio     SERIAL PRIMARY KEY,
    nome           VARCHAR(255) NOT NULL,
    data           DATE         NOT NULL,
    local          VARCHAR(255) NOT NULL,
    premio         INT          NOT NULL,
    estado_torneio VARCHAR(20)  NOT NULL DEFAULT 'Agendado',
    estado_admin   VARCHAR(20)  NOT NULL DEFAULT 'Não Aprovado'
);

CREATE TABLE Partidas
(
    id_partida     SERIAL PRIMARY KEY,
    id_torneio     INT REFERENCES Torneios (id_torneio)  NOT NULL,
    id_jogador_1   INT REFERENCES Jogadores (id_jogador) NOT NULL,
    id_jogador_2   INT REFERENCES Jogadores (id_jogador) NOT NULL,
    estado_partida VARCHAR(20)                           NOT NULL DEFAULT 'Agendado',
    ganhador       INT REFERENCES Jogadores (id_jogador)          DEFAULT NULL
);