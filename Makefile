# Makefile (en el directorio raíz del proyecto)

# Incluir la lógica base definida en makefiles.base
include makefiles.base

.PHONY: all build clean test publish

# Tarea por defecto que llama a la tarea gradle_build definida en la base
all: build

# Alias simples dentro de make para mayor comodidad
# Estos alias llaman a las reglas definidas en makefiles.base

build: gradle_build

clean: gradle_clean

test: gradle_test

publish: gradle_publish

# Puedes añadir otras tareas específicas aquí si es necesario
run:
	$(GRADLE_WRAPPER) run
