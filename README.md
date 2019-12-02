Dvojica:
Ján Horváth (433736)
Radka Sedláková (460676)

Funkcionalita:
- Ide o systém, ktorý slúži na požičiavanie replík fantasy predmetov a cosplayov (kostýmov fantasy postáv)
- Buttons (v pravém rohu) na hornom paneli prepínajú medzi bežným užívateľom a správcom/zamestnancom (simulace přístupu obou těchto entit k systému)
- Bežný užívateľ má prístup ku katalógu a k vytváraniu objednávky

- Správca/zamestnanec má prístup k zoznamu požičaných predmetov (v našom systéme označované ako objednávka)
- Správca môže zrušiť objednávku, čo znamená, že zákazník požičané predmety vrátil a môžu sa vrátiť naspäť do katalógu

- pro simulování času je implementována třída TimeSimulator, s níž souvisí buttony na horní liště, pomocí nichž je čas uměle posouván
- upozornění o nevrácené výpůjčce jsou vypisovány na chybový výstup a zároveň implementovány ve formě vyskakujícího okna (upozornění pro správce)
- s posouváním času souvisí kumulace většího množství vyskakujících oken (upozornění za každý den daného časového úseku). V reálné aplikaci by tato vlastnost nehrála roli - zaměstnanec by nenavrácené výpůjčky kontroloval denně.

TODO:
- cena se odvíjí od počtu dnů výpůjčky - implementovat přepočítávání ceny
- souhrn objednávky se objeví v okně Formulář

- kontrola vstupů ve Formuláři


