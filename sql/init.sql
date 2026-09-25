CREATE TABLE IF NOT EXISTS pacientes (
    dnipac    CHAR(9)      NOT NULL PRIMARY KEY,
    apelpac   VARCHAR(100) NOT NULL,
    nompac    VARCHAR(50)  NOT NULL,
    movilpac  VARCHAR(15),
    emailpac  VARCHAR(100),
    nacpac    DATE,
    dirpac    VARCHAR(150),
    propac    VARCHAR(50),
    munipac   VARCHAR(50)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
