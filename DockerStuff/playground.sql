SELECT J.*
FROM Jogadores J
         INNER JOIN Inscricoes I ON J.id_jogador = I.id_jogador
WHERE I.id_torneio = 1