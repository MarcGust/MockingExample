Syftet med laboration 2 är att träna på att skriva enhetstester, använda mockito för att skapa test doubles och skriva kod med hjälp av testdriven utveckling, TDD.

Uppgift1:
Skriv enhetstester (unit tests) för klassen BookingSystem. Försök att uppnå minst 90% code coverage.

Krav: 
1. Skriv tester med JUnit 5 och AssertJ
2. Skapa lämpliga test doubles för beroenden
5. Testa både lyckade och misslyckade scenarios
4. Använd parametriserade tester där det är lämpligt
5. Dokumentera dina testfall med tydliga beskrivningar

För VG gör dessutom: I paketet payment finns en utkommenterad klass som heter PaymentProcessor. Kommentera fram koden och gör den testbar. Beroenden finns inte tillgängliga nu utan behöver användas via interface där implementationen av dessa kommer finnas tillgänglig I ett senare skede. Modifiera koden så att den blir testbar och skriv tester för den.
• Identifiera och extrahera beroenden
• Tillämpa dependency injection
• Skriv tester för den refaktorerade koden
• Dokumentera dina refaktoreringsbeslut

Uppgift 2:
Implementera en ShoppingCart-klass med hjälp av TDD.

Implementationen ska stödja:
• Lägga till varor
• Ta bort varor
• Beräkna totalpris
• Applicera rabatter
• Hantera kvantitetsuppdateringar 

Krav: 
1. Följ TDD-cykeln: Red-Green-Refactor
2. Committa efter varje cykel
3. Använd beskrivande testnamn
4. Inkludera kant tester
