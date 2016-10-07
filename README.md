# Coverage Collector Plugin

This plug-in to collect and output the coverage data.

## Template Example

### Coverage Report

```
{% if masterSummary is empty %}
{{ ( summary.coverage  * 100 ) | numberformat("#.0#") }} %
{% else %}
{{ ( summary.coverage * 100 ) | numberformat("#.0#") }} % ( {{ (( summary.coverage - masterSummary.coverage ) * 100 ) | numberformat("#.0#") }} )
{% endif %}

| name | missed | covered | coverage |
|:----:|-------:|--------:|---------:|
| this | {{ summary.missed }} | {{ summary.covered }} | {{ ( summary.coverage * 100 ) | numberformat("#.0#") }} % |
{% if masterSummary is empty %}
| master | - | - | - | - | - |
{% else %}
| master | {{ masterSummary.missed }} | {{ masterSummary.covered }} | {{ ( masterSummary.coverage * 100 ) | numberformat("#.0#") }} % |
{% endif %}


## Details


| type | missed | covered | coverage |
|:----:|-------:|--------:|---------:|
{% for counter in report.counters %}
| {{ counter.type }} | {{ counter.missed }} | {{ counter.covered }} | {{ ( counter.coverage * 100 ) | numberformat("#.0#") }} % |
{% endfor %}
```

### JUnit Test Result

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
