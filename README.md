Dvojica:
Ján Horváth (433736)
Radka Sedláková (460676)

Funkcionalita:
- Ide o systém, ktorý slúži na požičiavanie replík fantasy predmetov a cosplayov (kostýmov fantasy postáv)
- Buttons (v pravém rohu) na hornom paneli prepínajú medzi bežným užívateľom a správcom/zamestnancom (simulace přístupu obou těchto entit k systému)
- Bežný užívateľ má prístup ku katalógu a k vytváraniu objednávky
- Vytváranie objednávky plánujeme _výrazně_ prekopať, konkrétne chceme zamedziť priamy prístup k formuláru (který je aktuálně přístupný přes button v horní liště) pukud nejsu zadány počty položek v tabulce objednávek a není kliknuto na  "Create Order", a premístiť prehľadávanie tabuľky z DataManager triedy jinam (vytvořit I/O třídu)
- v určitých fázích objednávání budou skryté odpovídající buttony na dolním toolbaru. Aktuálně systém funguje pouze po správném pořadí klikání buttonů (1. Create order, 2. Submit order)

- Správca/zamestnanec má prístup k zoznamu požičaných predmetov (v našom systéme označované ako objednávka)
- Správca môže zrušiť objednávku, čo znamená, že zákazník požičané predmety vrátil a môžu sa vrátiť naspäť do katalógu

