CREATE EXTENSION IF NOT EXISTS hstore;

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
        CHECK (id_jogador_1 < id_jogador_2),
    UNIQUE (id_torneio, id_jogador_1, id_jogador_2)
);

CREATE TABLE Inscricoes
(
    id_inscricao SERIAL PRIMARY KEY,
    id_jogador   INT REFERENCES Jogadores (id_jogador) NOT NULL,
    id_torneio   INT REFERENCES Torneios (id_torneio)  NOT NULL,
    UNIQUE (id_jogador, id_torneio)
);

-- https://cjauvin.blogspot.com/2013/05/impossibly-lean-audit-system-for.html
CREATE TABLE Auditoria
(
    id_auditoria SERIAL PRIMARY KEY,
    nome_tabela  TEXT      NOT NULL,
    id_jogador   INT       NOT NULL,
    operacao     TEXT      NOT NULL, -- INSERT, UPDATE, DELETE
    old_values   HSTORE,
    new_values   HSTORE,
    timestamp    TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE OR REPLACE FUNCTION audit_trigger() RETURNS trigger AS
$$
BEGIN
    IF (TG_OP = 'UPDATE') THEN
        INSERT INTO Auditoria (nome_tabela, id_jogador, operacao, old_values, new_values)
        VALUES (TG_TABLE_NAME, OLD.id_jogador, TG_OP, HSTORE(OLD), HSTORE(NEW));
        RETURN NEW;

    ELSIF (TG_OP = 'DELETE') THEN
        INSERT INTO Auditoria (nome_tabela, id_jogador, operacao, old_values)
        VALUES (TG_TABLE_NAME, OLD.id_jogador, TG_OP, HSTORE(OLD));
        RETURN OLD;

    ELSIF (TG_OP = 'INSERT') THEN
        INSERT INTO Auditoria (nome_tabela, id_jogador, operacao, new_values)
        VALUES (TG_TABLE_NAME, NEW.id_jogador, TG_OP, HSTORE(NEW));
        RETURN NEW;
    END IF;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_auditoria_jogadores
    AFTER INSERT OR UPDATE OR DELETE
    ON Jogadores
    FOR EACH ROW
EXECUTE FUNCTION audit_trigger();