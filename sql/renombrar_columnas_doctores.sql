-- Ejecutar UNA vez en phpMyAdmin (BD bbdd) para igualar las columnas al proyecto de referencia
ALTER TABLE doctores RENAME COLUMN nomedoc TO nomdoc;
ALTER TABLE doctores RENAME COLUMN maildoc TO emaildoc;
