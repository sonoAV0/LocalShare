# Relazione progetto — template LaTeX

## Come iniziare

1. Apri questa cartella in VS Code.
2. Installa l'estensione **LaTeX Workshop** (se non l'hai già fatta).
3. Assicurati di avere TeX Live installato sul sistema:
   ```
   sudo dnf install texlive-scheme-full      # Fedora
   ```
4. Apri `main.tex` e modifica i metadati in cima al file (righe con
   `\newcommand{\nomeProgetto}{...}` ecc.) con i tuoi dati reali.
5. Compila: in VS Code basta salvare il file (build automatica) oppure
   premere l'icona "play" di LaTeX Workshop.

## Struttura del progetto

```
relazione-progetto/
├── main.tex                  # documento principale (preambolo + frontespizio + include capitoli)
├── capitoli/
│   ├── introduzione.tex
│   ├── requisiti.tex
│   ├── tecnologie.tex
│   ├── progettazione.tex
│   ├── implementazione.tex
│   ├── funzionamento.tex
│   └── conclusioni.tex       # include anche la bibliografia
├── immagini/                 # metti qui screenshot, diagrammi, ecc.
├── codice/                   # eventuali file di codice da importare con \lstinputlisting
└── .gitignore
```

## Come inserire un'immagine

```latex
\begin{figure}[h]
    \centering
    \includegraphics[width=0.8\textwidth]{nome_immagine.png}
    \caption{Descrizione dell'immagine}
    \label{fig:etichetta}
\end{figure}
```

Metti il file immagine dentro `immagini/`: il path è già configurato nel
preambolo di `main.tex` (`\graphicspath{{immagini/}}`), quindi basta
richiamare il nome del file senza percorso.

## Come importare codice da un file esterno (consigliato)

Invece di copiare a mano il codice nel `.tex`, in `implementazione.tex`
puoi usare:

```latex
\lstinputlisting[language=Python, caption={Modulo principale}]{codice/main.py}
```

così eviti errori di trascrizione e il codice resta sempre sincronizzato
con il file originale.

## Note sulla lunghezza

I capitoli sono già abbozzati per arrivare a circa 10-15 pagine totali con
contenuto pieno: introduzione (~1 pag), requisiti (~1-2 pag), tecnologie
(~1-2 pag), progettazione (~2-3 pag), implementazione (~3-4 pag),
funzionamento (~2-3 pag), conclusioni (~1 pag).
