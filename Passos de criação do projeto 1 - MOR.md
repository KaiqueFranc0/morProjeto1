Passos de criação do projeto 1 - MOR:
1- criação do database mySQL morProjeto1
2- criação do projeto java maven
3- no pom.xlm, atualizar o maven para java 11
4- inclusão das dependências hibernate e mysSQL
5- criação do arquivo persitence.xml
6- criação as classes model Denuncia e Instituição
7- definição dos relacionamentos ManyToOne e OneToMany (varias Denuncias para uma Instituição com relacionamento bidirecional)
8- criação das classes controller contendo crud pra denuncia e cr pra instituição, as classes de controle não fazem verificação de formato de dados, apenas verificação de nulos para instancias de denuncia e instituição para manter os relacionamentos saudáveis. Além disso, não lidam diretamente com scanner, a view recebe e valida os dados, e passa como parâmetro para a classe de controle
9- criação da view com construtor recebendo os objetos scanner, denunciaController e instituicaoController para utilizar os métodas controller de forma não estática. a view faz verificação de alguns formatos de dados, imprime erros e resultados esperados, e faz a chamada dos métodos controller.
10- criação da main que cria os objetos scanner, denunciaController, instituicaoController e view. cria a fabrica entity manager e chama o view para permitir a interação do usuario. também printa erros de inicialização e fecha a emf ao encerrar. 
