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

CREATE TABLE IF NOT EXISTS citas (
    idcita       INT          NOT NULL AUTO_INCREMENT PRIMARY KEY,
    citadnipac   VARCHAR(9)   NOT NULL,
    citaiddoc    INT          NOT NULL,
    fechacita    DATE         NOT NULL,
    horacita     VARCHAR(8)   NOT NULL,
    motivo       VARCHAR(200) DEFAULT NULL,
    diagnostico  TEXT,
    tratamiento  TEXT,
    estadocita   VARCHAR(20)  DEFAULT 'Pendiente',
    KEY dnipac (citadnipac),
    KEY iddoc (citaiddoc),
    FOREIGN KEY (citadnipac) REFERENCES pacientes (dnipac),
    FOREIGN KEY (citaiddoc)  REFERENCES doctores (iddoc)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
