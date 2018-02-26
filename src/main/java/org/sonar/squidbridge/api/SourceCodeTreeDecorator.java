/*
 * SSLR Squid Bridge
 * Copyright (C) 2010-2018 SonarSource SA
 * mailto:info AT sonarsource DOT com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package org.sonar.squidbridge.api;

import org.sonar.squidbridge.measures.MetricDef;

public class SourceCodeTreeDecorator {

  private final SourceProject project;

  public SourceCodeTreeDecorator(SourceProject project) {
    this.project = project;
  }

  public void decorateWith(MetricDef... metrics) {
    decorateWith(project, metrics);
  }

  private void decorateWith(SourceCode sourceCode, MetricDef... metrics) {
    if (sourceCode.hasChildren()) {
      for (SourceCode child : sourceCode.getChildren()) {
        decorateWith(child, metrics);
      }
    }
    for (MetricDef metric : metrics) {
      if (!metric.aggregateIfThereIsAlreadyAValue() && Double.doubleToRawLongBits(sourceCode.getDouble(metric)) != 0) {
        continue;
      }
      if (sourceCode.hasChildren()) {
        for (SourceCode child : sourceCode.getChildren()) {
          if (!metric.isCalculatedMetric() && metric.isThereAggregationFormula()) {
            sourceCode.add(metric, child);
          }
        }
      }
    }
  }
}
