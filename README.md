# Coverage Collector Plugin

This plug-in to collect and output the coverage data.

## Template Example

```
{% if masterTestTotal is empty %}
{{ ( testTotal.coverage  * 100 ) | numberformat("#.0#") }} %
{% else %}
{{ ( testTotal.coverage * 100 ) | numberformat("#.0#") }} % ( {{ (( testTotal.coverage - masterTestTotal.coverage ) * 100 ) | numberformat("#.0#") }} )
{% endif %}



| name | tests | errors | failures | skipped | coverage |
|:----:|------:|-------:|---------:|--------:|---------:|
| this | {{ testTotal.tests }} | {{ testTotal.errors }} | {{ testTotal.failures }} | {{ testTotal.skipped }} | {{ ( testTotal.coverage * 100 ) | numberformat("#.0#") }} % |
{% if masterTestTotal is empty %}
| master | - | - | - | - | - |
{% else %}
| master | {{ masterTestTotal.tests }} | {{ masterTestTotal.errors }} | {{ masterTestTotal.failures }} | {{ masterTestTotal.skipped }} | {{ ( masterTestTotal.coverage * 100 ) | numberformat("#.0#") }} % |
{% endif %}

{% if testSuites is not empty %}
## Details of this pull request

| name | tests | errors | failures | skipped |
|:----:|------:|-------:|---------:|--------:|
{% for testSuite in testSuites %}
| {{ testSuite.name }} | {{ testSuite.tests }} | {{ testSuite.errors }} | {{ testSuite.failures }} | {{ testSuite.skipped }} |
{% endfor %}
{% endif %}

{% if masterTestSuites is not empty %}
## Details of target

| name | tests | errors | failures | skipped |
|:----:|------:|-------:|---------:|--------:|
{% for testSuite in masterTestSuites %}
| {{ testSuite.name }} | {{ testSuite.tests }} | {{ testSuite.errors }} | {{ testSuite.failures }} | {{ testSuite.skipped }} | - |
{% endfor %}
{% endif %}
```
